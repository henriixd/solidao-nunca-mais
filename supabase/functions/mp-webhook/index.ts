// Recebe os avisos do Mercado Pago e registra o pagamento sozinho na tabela `pagamentos`.
//
// Segurança: o conteúdo do aviso NÃO é usado. Pegamos só o id da order e consultamos
// o próprio Mercado Pago com o nosso Access Token. Um aviso falso não tem como virar
// pagamento, porque a order precisa existir na nossa conta e estar paga.
//
// Segredos: MP_ACCESS_TOKEN (o mesmo da função criar-pagamento).
// Esta função precisa estar com "Verify JWT" DESLIGADO (o Mercado Pago não manda login).
import { createClient } from "npm:@supabase/supabase-js@2";

const UUID = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

function metodo(pg: any): string {
  const id = String(pg?.payment_method?.id || "").toLowerCase();
  const tipo = String(pg?.payment_method?.type || "").toLowerCase();
  if (id === "pix" || tipo === "bank_transfer") return "Pix";
  if (tipo === "debit_card") return "Cartão de débito";
  if (tipo.includes("card")) return "Cartão de crédito";
  return "Mercado Pago";
}

Deno.serve(async (req) => {
  const url = new URL(req.url);
  let corpo: any = {};
  try { corpo = await req.json(); } catch { /* sem corpo */ }

  const tipo = url.searchParams.get("type") || corpo?.type;
  const id = url.searchParams.get("data.id") || corpo?.data?.id;
  // Responde 200 a qualquer coisa que não seja order, para o Mercado Pago não ficar reenviando.
  if (tipo !== "order" || !id) return new Response("ignorado", { status: 200 });

  const token = Deno.env.get("MP_ACCESS_TOKEN");
  if (!token) return new Response("sem token", { status: 500 });

  const r = await fetch(`https://api.mercadopago.com/v1/orders/${encodeURIComponent(id)}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!r.ok) {
    console.error("Não consegui consultar a order", id, r.status, await r.text());
    // 500 faz o Mercado Pago tentar de novo mais tarde.
    return new Response("erro ao consultar", { status: 500 });
  }
  const ordem = await r.json();

  // Order paga = status "processed". Qualquer outro estado (criada, pendente, recusada…) só é ignorado.
  if (ordem.status !== "processed") return new Response("ainda não pago", { status: 200 });

  const [cadastroId, parId] = String(ordem.external_reference || "").split("|");
  if (!UUID.test(cadastroId || "")) {
    console.error("Order paga sem referência válida", id, ordem.external_reference);
    return new Response("referência inválida", { status: 200 });
  }

  const pg = ordem.transactions?.payments?.[0];
  const db = createClient(Deno.env.get("SUPABASE_URL")!, Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!);
  const { error } = await db.from("pagamentos").upsert({
    cadastro_id: cadastroId,
    par_id: UUID.test(parId || "") ? parId : null,
    valor: Number(ordem.total_paid_amount || ordem.total_amount),
    metodo: metodo(pg),
    mp_order_id: ordem.id,
    observacao: `Mercado Pago (automático)${ordem.live_mode === false ? " · TESTE" : ""}`,
  }, { onConflict: "mp_order_id", ignoreDuplicates: true });

  if (error) {
    console.error("Erro ao gravar pagamento", id, error);
    return new Response("erro ao gravar", { status: 500 });
  }
  return new Response("ok", { status: 200 });
});
