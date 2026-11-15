-- Pagamentos automáticos pelo Mercado Pago.
-- Rode uma vez no Supabase: SQL Editor → New query → cole tudo → Run.

-- Guarda o id da order do Mercado Pago, para o mesmo pagamento nunca ser registrado duas vezes.
alter table public.pagamentos add column if not exists mp_order_id text;
create unique index if not exists pagamentos_mp_order_id_key on public.pagamentos (mp_order_id);

-- Garante a coluna de observação (o painel já exibe).
alter table public.pagamentos add column if not exists observacao text;
