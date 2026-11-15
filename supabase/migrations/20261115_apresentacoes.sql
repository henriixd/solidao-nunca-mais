-- Acompanhamento das apresentações (depois que os dois pagaram).
create table if not exists public.apresentacoes (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  a_id uuid not null references public.cadastros(id) on delete cascade,
  b_id uuid not null references public.cadastros(id) on delete cascade,
  status text not null default 'contatar' check (status in
    ('contatar', 'aguardando', 'encontro_marcado', 'contato_trocado', 'aconteceu', 'deu_certo', 'nao_rolou', 'cancelado')),
  modo text check (modo in ('encontro', 'whatsapp')),
  local text check (char_length(local) <= 300),
  data_encontro timestamptz,
  anotacoes text check (char_length(anotacoes) <= 4000),
  check (a_id < b_id),
  unique (a_id, b_id)
);

alter table public.apresentacoes enable row level security;
drop policy if exists "admins gerenciam apresentacoes" on public.apresentacoes;
create policy "admins gerenciam apresentacoes" on public.apresentacoes
  for all to authenticated using (public.is_admin()) with check (public.is_admin());
revoke all on public.apresentacoes from anon;
grant select, insert, update, delete on public.apresentacoes to authenticated;
revoke truncate, references, trigger on public.apresentacoes from authenticated;

create or replace function public._apresentacao_updated_at()
returns trigger language plpgsql set search_path = public as $$
begin new.updated_at := now(); return new; end $$;
revoke all on function public._apresentacao_updated_at() from public, anon, authenticated;
drop trigger if exists apresentacao_updated_at on public.apresentacoes;
create trigger apresentacao_updated_at before update on public.apresentacoes
  for each row execute function public._apresentacao_updated_at();
