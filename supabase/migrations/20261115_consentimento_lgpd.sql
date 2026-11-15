-- Registro do consentimento (LGPD): aceite dos termos e autorização específica para dados sensíveis.
alter table public.cadastros add column if not exists termos_versao text;
alter table public.cadastros add column if not exists aceite_termos_em timestamptz;
alter table public.cadastros add column if not exists consentimento_sensiveis_em timestamptz;

-- A trava do cadastro público passa a exigir os dois consentimentos e grava a hora oficial do servidor.
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

  -- Consentimento obrigatório (LGPD art. 7º, I e art. 11, I).
  if new.aceite_termos_em is null or new.consentimento_sensiveis_em is null
     or coalesce(new.termos_versao, '') !~ '^\d{4}-\d{2}-\d{2}$' then
    raise exception 'SNM:termos' using errcode = 'P0001';
  end if;
  new.aceite_termos_em := now();
  new.consentimento_sensiveis_em := now();

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

-- A view do painel mostra as colunas novas.
create or replace view public.cadastros_admin with (security_invoker = true) as
select c.id, c.created_at, c.nome, c.email, c.whatsapp, c.nascimento, c.genero, c.cidade, c.uf, c.altura, c.corpo, c.pele,
  c.olhos, c.cabelo, c.estado_civil, c.filhos, c.quer_filhos, c.escolaridade, c.fuma, c.bebe, c.atividade, c.pets,
  c.religiao, c.religiao_importancia, c.politica, c.personalidade, c.interesses, c.procura_genero, c.intencao,
  c.idade_min, c.idade_max, c.distancia, c.pref_altura_min, c.pref_altura_max, c.pref_corpo, c.deseja, c.pref_religiao,
  c.pref_politica, c.pref_fuma, c.pref_bebe, c.pref_filhos, c.sobre, c.pessoa_ideal, c.status, c.observacoes,
  extract(year from age(c.nascimento::timestamptz))::int as idade,
  exists (select 1 from public.pagamentos p where p.cadastro_id = c.id) as ja_pagou,
  (select count(*) from public.pagamentos p where p.cadastro_id = c.id)::int as qtd_pagamentos,
  c.termos_versao, c.aceite_termos_em, c.consentimento_sensiveis_em
from public.cadastros c;
revoke all on public.cadastros_admin from anon;
revoke truncate, references, trigger on public.cadastros_admin from authenticated;
