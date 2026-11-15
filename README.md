# Solidão Nunca Mais 🌹🌹

Site da agência de relacionamentos **Solidão Nunca Mais**.

O logo une duas rosas: a **vermelha** simboliza o **amor** e a **amarela** simboliza a **fidelidade**. A agência é aberta a todos os casais, inclusive do mesmo sexo.

## Como funciona o serviço

1. A pessoa faz o cadastro gratuito contando **como ela é**.
2. Informa **que tipo de pessoa deseja** conhecer.
3. O sistema faz o cruzamento de dados e avisa a equipe quando há compatibilidade (nos dois sentidos).
4. A equipe avisa a pessoa que existe alguém compatível — gratuitamente.
5. Se quiser conhecer, ela paga **R$ 70** via Pix ou cartão de crédito.
6. Quando **ambas** pagam, o contato é liberado ou é marcado um **encontro às cegas**.

## Estrutura

```
index.html        Site público com o cadastro (grava no Supabase)
admin.html        Painel admin: cadastros com filtros, pares compatíveis,
                  busca manual para amigos, pagamentos e links de pagamento
pagar.html        Página de pagamento da taxa (Mercado Pago · Checkout Pro)
supabase/         Funções (Edge Functions) e alterações do banco
assets/config.js  URL e chave pública do Supabase
assets/campos.js  Opções do cadastro (usadas pelo site e pelo painel)
assets/           Imagens das rosas e logo
```

## Banco de dados (Supabase: projeto `solidao-nunca-mais`)

- `cadastros`: todos os cadastros do site. Visitantes só podem **inserir**; apenas admins leem, editam e apagam.
- `pagamentos`: pagamentos registrados por cadastro (a coluna "Já pagou" do painel vem daqui).
- `admins`: e-mails com acesso ao painel. Para adicionar um admin, crie o usuário em
  *Authentication → Users* no Supabase e rode: `insert into admins (email) values ('email@exemplo.com');`
- `compatibilidades()`: pares compatíveis nos dois sentidos. Inegociáveis: gênero, idade, distância, religião, política, cigarro e filhos. Pontuação: personalidade, interesses, tipo físico, altura, bebida, vontade de ter filhos e intenção.
- `buscar_para_amigo(...)`: busca manual no banco para alguém que não está cadastrado.
- `pares_manuais`: pares formados pela equipe, que aparecem na lista mesmo fora dos critérios automáticos.
- `analisar_par(x, y)`: compara duas pessoas quaisquer e explica, nos dois sentidos, qual inegociável barra o par e a afinidade no restante.

## Pagamentos (Mercado Pago)

1. No painel, abra o cadastro, vá em **Pagamentos** e copie o link do par (🔗) ou envie direto no WhatsApp (💬).
2. A pessoa abre `pagar.html`, clica em **Quero conhecer** e paga no Mercado Pago (Pix ou cartão).
3. O Mercado Pago avisa a função `mp-webhook`, que registra o pagamento sozinho. Ele aparece no painel como "Já pagou".

Peças no Supabase:
- `supabase/migrations/20261114_pagamentos_mercado_pago.sql`: rodar uma vez no SQL Editor.
- Edge Functions `criar-pagamento` e `mp-webhook`, as duas com **Verify JWT desligado**.
- Secret `MP_ACCESS_TOKEN`: Access Token do Mercado Pago. Para sair do modo teste, basta trocar pelo token de produção.
- Webhook no Mercado Pago: Suas integrações → aplicação → **Webhooks → Configurar notificações**, evento **Order (Mercado Pago)**, URL `https://fahybflzrylcbbavgczv.supabase.co/functions/v1/mp-webhook` (configurar nas abas de teste e de produção).
- Opcionais: `VALOR_TAXA` (padrão 70) e `SITE_URL` (padrão https://www.solidaonuncamais.com.br).

Para ver, basta abrir o `index.html` no navegador. Também funciona direto no GitHub Pages.

## Segurança

- Visitantes só podem **inserir** cadastros (sem ler, alterar ou apagar). Tabelas novas não ficam abertas por padrão: conceda permissões explicitamente.
- Admin = usuário com e-mail confirmado no Supabase Auth **e** listado em `admins` (a tabela só se edita pelo SQL Editor).
- Trigger `proteger_cadastro`: barra e-mail/WhatsApp repetido, textos gigantes e mais de 20 cadastros em 10 minutos no site todo.
- `criar-pagamento` só cobra de pares válidos (`par_valido`): compatíveis pelos critérios ou formados no painel.
- `mp-webhook` não confia no conteúdo do aviso: consulta a order direto no Mercado Pago.
- Páginas com Content-Security-Policy e proteção contra serem abertas dentro de outro site (iframe).

## Próximos passos

- [x] Salvar os cadastros no Supabase
- [x] Cruzamento de dados
- [x] Painel admin
- [x] Pagamento via Pix e cartão (Mercado Pago)
- [ ] Liberação do contato / agendamento do encontro após ambos pagarem
- [ ] Página de política de privacidade (LGPD) e termos de uso
