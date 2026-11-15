-- Endurecimento de segurança do banco.

-- 1) Permissões mínimas. Visitantes (anon) só podem INSERIR cadastros; nada mais.
--    TRUNCATE/TRIGGER/REFERENCES não passam pelas regras de RLS, então ninguém do lado de fora fica com eles.
revoke all on public.cadastros, public.cadastros_admin, public.pagamentos, public.pares_manuais, public.admins from anon;
grant insert on public.cadastros to anon;
revoke truncate, references, trigger on public.cadastros, public.cadastros_admin, public.pagamentos,
  public.pares_manuais, public.admins from authenticated;
-- A tabela de admins não é editável pelo site: só pelo SQL Editor do Supabase.
revoke insert, update, delete on public.admins from authenticated;
-- Tabelas novas no schema public não ficam abertas para visitantes por padrão.
alter default privileges in schema public revoke all on tables from anon;
alter default privileges in schema public revoke truncate, references, trigger on tables from authenticated;

-- 2) Admin = usuário logado, com e-mail CONFIRMADO, cujo e-mail está na tabela admins.
create or replace function public.is_admin()
returns boolean language sql stable security definer set search_path = public as $$
  select exists (
    select 1 from auth.users u
    join public.admins a on lower(a.email) = lower(u.email)
    where u.id = auth.uid() and u.email_confirmed_at is not null
  );
$$;

-- 3) Proteção do formulário público de cadastro.
create index if not exists cadastros_email_lower_idx on public.cadastros (lower(email));
create index if not exists cadastros_created_at_idx on public.cadastros (created_at);

create or replace function public._proteger_cadastro()
returns trigger language plpgsql security definer set search_path = public as $$
declare
  recentes int;
  zap text := right(regexp_replace(coalesce(new.whatsapp, ''), '\D', '', 'g'), 11);
  arrays text[] := array['personalidade','interesses','pref_corpo','deseja','pref_religiao','pref_politica','pref_fuma','pref_bebe'];
begin
  -- Admins (painel) não passam por estas travas.
  if public.is_admin() then return new; end if;

  new.created_at := now();

  -- Tamanho: impede encher o banco com textos gigantes.
  if octet_length(row_to_json(new)::text) > 20000 then
    raise exception 'SNM:grande' using errcode = 'P0001';
  end if;
  if exists (select 1 from json_each_text(row_to_json(new)) j
             where j.key not in ('sobre', 'pessoa_ideal') and not j.key = any(arrays) and length(j.value) > 200) then
    raise exception 'SNM:grande' using errcode = 'P0001';
  end if;
  if exists (select 1 from unnest(new.personalidade || new.interesses || new.pref_corpo || new.deseja ||
                                  new.pref_religiao || new.pref_politica || new.pref_fuma || new.pref_bebe) e
             where length(e) > 80) then
    raise exception 'SNM:grande' using errcode = 'P0001';
  end if;

  -- Duplicado: mesmo e-mail ou mesmo WhatsApp.
  if exists (select 1 from public.cadastros c
             where lower(c.email) = lower(new.email)
                or (length(zap) >= 10 and right(regexp_replace(coalesce(c.whatsapp, ''), '\D', '', 'g'), 11) = zap)) then
    raise exception 'SNM:duplicado' using errcode = 'P0001';
  end if;

  -- Freio contra robôs: no máximo 20 cadastros novos a cada 10 minutos no site todo.
  select count(*) into recentes from public.cadastros where created_at > now() - interval '10 minutes';
  if recentes >= 20 then
    raise exception 'SNM:limite' using errcode = 'P0001';
  end if;

  return new;
end $$;
revoke all on function public._proteger_cadastro() from public, anon, authenticated;

drop trigger if exists proteger_cadastro on public.cadastros;
create trigger proteger_cadastro before insert on public.cadastros
  for each row execute function public._proteger_cadastro();

-- 4) Usado pela função de pagamento: só cobra de pares de verdade (compatíveis ou formados pela equipe).
create or replace function public.par_valido(x uuid, y uuid)
returns boolean language plpgsql stable security definer set search_path = public as $$
declare a public.cadastros; b public.cadastros;
begin
  select * into a from public.cadastros where id = least(x, y);
  select * into b from public.cadastros where id = greatest(x, y);
  if a.id is null or b.id is null or a.id = b.id then return false; end if;
  if exists (select 1 from public.pares_manuais m where m.a_id = a.id and m.b_id = b.id) then return true; end if;
  return a.status = 'ativo' and b.status = 'ativo'
    and cardinality(_bloqueios(a, b, extract(year from age(b.nascimento))::int)) = 0
    and cardinality(_bloqueios(b, a, extract(year from age(a.nascimento))::int)) = 0;
end $$;
revoke all on function public.par_valido(uuid, uuid) from public, anon, authenticated;
grant execute on function public.par_valido(uuid, uuid) to service_role;
