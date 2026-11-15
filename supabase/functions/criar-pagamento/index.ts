// Cria a cobrança de R$ 70 no Mercado Pago (Checkout Pro / API de Orders)
// e devolve o link para onde o cliente deve ser levado.
//
// Chamado pela página pagar.html com { c: <id do cadastro>, p: <id do par> }.
// Segredos necessários (Supabase → Edge Functions → Secrets):
//   MP_ACCESS_TOKEN  Access Token do Mercado Pago (teste ou produção)
//   SITE_URL         ex.: https://www.solidaonuncamais.com.br  (opcional)
//   VALOR_TAXA       ex.: 70.00                                (opcional)
import { createClient } from "npm:@supabase/supabase-js@2";

const cors = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};
const json = (body: unknown, status = 200) =>
  new Response(JSON.stringify(body), { status, headers: { ...cors, "Content-Type": "application/json" } });

const UUID = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: cors });
  if (req.method !== "POST") return json({ erro: "Método não permitido" }, 405);

  const token = Deno.env.get("MP_ACCESS_TOKEN");
  if (!token) return json({ erro: "Pagamento ainda não configurado" }, 500);
  const site = (Deno.env.get("SITE_URL") || "https://www.solidaonuncamais.com.br").replace(/\/$/, "");
  const valor = Number(Deno.env.get("VALOR_TAXA") || "70").toFixed(2);

  let c = "", p = "", consultar = false;
  try { ({ c, p, consultar } = await req.json()); } catch { /* corpo inválido */ }
  if (!UUID.test(c || "") || !UUID.test(p || "")) return json({ erro: "Link de pagamento inválido" }, 400);

  const db = createClient(Deno.env.get("SUPABASE_URL")!, Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!);

  const { data: pessoas, error } = await db.from("cadastros").select("id, nome, email").in("id", [c, p]);
  if (error) return json({ erro: "Erro ao consultar cadastro" }, 500);
  const quem = pessoas?.find((x) => x.id === c);
  if (!quem || !pessoas?.some((x) => x.id === p)) return json({ erro: "Link de pagamento inválido" }, 404);

  const primeiroNome = String(quem.nome || "").trim().split(/\s+/)[0];

  // Já pagou por esse par? Não cobra de novo.
  const { count } = await db.from("pagamentos").select("id", { count: "exact", head: true })
    .eq("cadastro_id", c).eq("par_id", p);
  if (count && count > 0) return json({ nome: primeiroNome, valor, ja_pago: true });

  // A página só quer mostrar o nome e o valor antes do clique.
  if (consultar) return json({ nome: primeiroNome, valor, ja_pago: false });

  const funcoes = `${Deno.env.get("SUPABASE_URL")}/functions/v1`;
  const volta = `${site}/pagar.html?c=${c}&p=${p}`;

  const resp = await fetch("https://api.mercadopago.com/v1/orders", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      "X-Idempotency-Key": crypto.randomUUID(),
    },
    body: JSON.stringify({
      type: "online",
      processing_mode: "manual",
      total_amount: valor,
      external_reference: `${c}|${p}`,
      description: "Solidão Nunca Mais · Taxa de apresentação",
      expiration_time: "P3D",
      payer: quem.email ? { email: quem.email } : undefined,
      items: [{ title: "Taxa de apresentação · Solidão Nunca Mais", unit_price: valor, quantity: 1 }],
      config: {
        notification_url: `${funcoes}/mp-webhook`,
        online: {
          success_url: `${volta}&r=aprovado`,
          pending_url: `${volta}&r=pendente`,
          failure_url: `${volta}&r=recusado`,
          auto_return: "all",
        },
      },
    }),
  });
  const ordem = await resp.json().catch(() => ({}));
  if (!resp.ok || !ordem.checkout_url) {
    console.error("Mercado Pago recusou a criação da order", resp.status, JSON.stringify(ordem));
    return json({ erro: "Não foi possível gerar o pagamento agora. Tente novamente em instantes." }, 502);
  }
  return json({ nome: primeiroNome, valor, url: ordem.checkout_url });
});
