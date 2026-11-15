-- Pareamento manual: a equipe analisa duas pessoas e pode formar o par mesmo fora dos critérios automáticos.

-- 1) Motivos pelos quais "p" recusaria "c" (os itens inegociáveis). Lista vazia = aceita.
create or replace function public._bloqueios(p public.cadastros, c public.cadastros, c_idade int)
returns text[] language plpgsql stable set search_path = public as $$
declare r text[] := '{}';
begin
  if not (p.procura_genero = 'Tanto faz' or p.procura_genero = c.genero) then
    r := r || format('Gênero: procura %s, a outra pessoa é %s', lower(p.procura_genero), lower(coalesce(c.genero, '—')));
  end if;
  if c_idade not between p.idade_min and p.idade_max then
    r := r || format('Idade: aceita de %s a %s anos, a outra pessoa tem %s', p.idade_min,
      case when p.idade_max >= 120 then '80+' else p.idade_max::text end, c_idade);
  end if;
  if p.distancia = 'Mesma cidade' and (_norm(p.cidade) <> _norm(c.cidade) or p.uf <> c.uf) then
    r := r || format('Distância: só aceita a mesma cidade (%s/%s × %s/%s)', p.cidade, p.uf, c.cidade, c.uf);
  end if;
  if p.distancia = 'Mesmo estado' and p.uf <> c.uf then
    r := r || format('Distância: só aceita o mesmo estado (%s × %s)', p.uf, c.uf);
  end if;
  if cardinality(p.pref_religiao) > 0 and c.religiao is not null and not c.religiao = any(p.pref_religiao) then
    r := r || format('Religião: aceita %s; a outra pessoa é %s', array_to_string(p.pref_religiao, ', '), c.religiao);
  end if;
  if cardinality(p.pref_politica) > 0 and c.politica is not null and not c.politica = any(p.pref_politica) then
    r := r || format('Política: aceita %s; a outra pessoa é %s', array_to_string(p.pref_politica, ', '), c.politica);
  end if;
  if cardinality(p.pref_fuma) > 0 and c.fuma is not null and not c.fuma = any(p.pref_fuma) then
    r := r || format('Cigarro: aceita %s; a outra pessoa: %s', array_to_string(p.pref_fuma, ', '), c.fuma);
  end if;
  if p.pref_filhos = 'Prefiro quem não tem filhos' and c.filhos like 'Tenho%' then
    r := r || format('Filhos: prefere quem não tem filhos; a outra pessoa: %s', c.filhos);
  end if;
  return r;
end $$;

-- 2) Pontos de afinidade (0–100) sem olhar os inegociáveis.
create or replace function public._pontos(p public.cadastros, c public.cadastros)
returns int language plpgsql stable set search_path = public as $$
declare
  peso numeric := 0; soma numeric := 0; n int;
begin
  if cardinality(p.deseja) > 0 then
    n := cardinality(array(select unnest(p.deseja) intersect select unnest(c.personalidade)));
    peso := peso + 30; soma := soma + 30 * n::numeric / cardinality(p.deseja);
  end if;
  if cardinality(p.interesses) > 0 and cardinality(c.interesses) > 0 then
    n := cardinality(array(select unnest(p.interesses) intersect select unnest(c.interesses)));
    peso := peso + 20; soma := soma + 20 * least(1, n::numeric / least(3, least(cardinality(p.interesses), cardinality(c.interesses))));
  end if;
  if cardinality(p.pref_corpo) > 0 and c.corpo is not null then
    peso := peso + 15; soma := soma + case when c.corpo = any(p.pref_corpo) then 15 else 0 end;
  end if;
  if (p.pref_altura_min is not null or p.pref_altura_max is not null) and c.altura is not null then
    peso := peso + 10; soma := soma + case when c.altura between coalesce(p.pref_altura_min,0) and coalesce(p.pref_altura_max,999) then 10 else 0 end;
  end if;
  if cardinality(p.pref_bebe) > 0 and c.bebe is not null then
    peso := peso + 10; soma := soma + case when c.bebe = any(p.pref_bebe) then 10 else 0 end;
  end if;
  if p.quer_filhos is not null and c.quer_filhos is not null and p.quer_filhos <> 'Ainda não sei' and c.quer_filhos <> 'Ainda não sei' then
    peso := peso + 10; soma := soma + case when p.quer_filhos = c.quer_filhos then 10 else 0 end;
  end if;
  if p.religiao_importancia = 'Muito importante' and p.religiao is not null then
    peso := peso + 10; soma := soma + case when p.religiao = c.religiao then 10 else 0 end;
  end if;
  if peso = 0 then return 50; end if;
  return round(100 * soma / peso)::int;
end $$;

-- 3) _afinidade continua igual por fora (usada também na busca para amigo), agora montada com as duas acima.
create or replace function public._afinidade(p public.cadastros, c public.cadastros, c_idade integer)
returns integer language plpgsql stable set search_path = public as $$
begin
  if cardinality(_bloqueios(p, c, c_idade)) > 0 then return -1; end if;
  return _pontos(p, c);
end $$;

-- 4) Pares formados manualmente pela equipe.
create table if not exists public.pares_manuais (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now(),
  a_id uuid not null references public.cadastros(id) on delete cascade,
  b_id uuid not null references public.cadastros(id) on delete cascade,
  observacao text,
  criado_por text default (auth.jwt() ->> 'email'),
  check (a_id < b_id),
  unique (a_id, b_id)
);
alter table public.pares_manuais enable row level security;
drop policy if exists "admins gerenciam pares manuais" on public.pares_manuais;
create policy "admins gerenciam pares manuais" on public.pares_manuais
  for all to authenticated using (public.is_admin()) with check (public.is_admin());
grant select, insert, update, delete on public.pares_manuais to authenticated;

-- 5) Análise de duas pessoas quaisquer (para a tela de pareamento manual).
create or replace function public.analisar_par(x uuid, y uuid)
returns jsonb language plpgsql stable security definer set search_path = public as $$
declare a public.cadastros; b public.cadastros; bab text[]; bba text[]; pab int; pba int;
begin
  if not public.is_admin() then raise exception 'acesso negado'; end if;
  select * into a from public.cadastros where id = least(x, y);
  select * into b from public.cadastros where id = greatest(x, y);
  if a.id is null or b.id is null or a.id = b.id then raise exception 'escolha duas pessoas diferentes'; end if;
  bab := _bloqueios(a, b, extract(year from age(b.nascimento))::int);
  bba := _bloqueios(b, a, extract(year from age(a.nascimento))::int);
  pab := _pontos(a, b); pba := _pontos(b, a);
  return jsonb_build_object(
    'a_id', a.id, 'b_id', b.id,
    'bloqueios_ab', to_jsonb(bab), 'bloqueios_ba', to_jsonb(bba),
    'afinidade_ab', pab, 'afinidade_ba', pba,
    'pontuacao', _pontuacao_par(pab, pba, a.intencao, b.intencao),
    'mesma_intencao', coalesce(a.intencao = b.intencao, false),
    'automatico', cardinality(bab) = 0 and cardinality(bba) = 0,
    'manual', exists (select 1 from public.pares_manuais m where m.a_id = a.id and m.b_id = b.id));
end $$;

-- 6) Lista de pares: automáticos + manuais (com as colunas novas "manual" e "automatico").
drop function if exists public.compatibilidades(integer);
create function public.compatibilidades(p_min integer default 0)
returns table(a_id uuid, b_id uuid, pontuacao integer, afinidade_ab integer, afinidade_ba integer,
              mesma_intencao boolean, manual boolean, automatico boolean)
language plpgsql stable security definer set search_path = public as $$
begin
  if not public.is_admin() then raise exception 'acesso negado'; end if;
  return query
  select a.id, b.id, x.pontos, x.ab, x.ba, coalesce(a.intencao = b.intencao, false), x.manual, x.auto
  from public.cadastros a
  join public.cadastros b on a.id < b.id
  cross join lateral (select
      _bloqueios(a, b, extract(year from age(b.nascimento))::int) as bab,
      _bloqueios(b, a, extract(year from age(a.nascimento))::int) as bba,
      _pontos(a, b) as pab, _pontos(b, a) as pba,
      exists (select 1 from public.pares_manuais m where m.a_id = a.id and m.b_id = b.id) as manual) y
  cross join lateral (select
      case when cardinality(y.bab) > 0 then -1 else y.pab end as ab,
      case when cardinality(y.bba) > 0 then -1 else y.pba end as ba,
      _pontuacao_par(y.pab, y.pba, a.intencao, b.intencao) as pontos,
      cardinality(y.bab) = 0 and cardinality(y.bba) = 0 as auto,
      y.manual) x
  where a.status = 'ativo' and b.status = 'ativo'
    and (x.manual or (x.auto and x.pontos >= p_min))
  order by x.manual desc, 3 desc;
end $$;

-- Permissões: só usuários logados (e o painel ainda confere is_admin).
revoke all on function public._bloqueios(public.cadastros, public.cadastros, int) from public, anon;
revoke all on function public._pontos(public.cadastros, public.cadastros) from public, anon;
revoke all on function public.analisar_par(uuid, uuid) from public, anon;
revoke all on function public.compatibilidades(integer) from public, anon;
grant execute on function public._bloqueios(public.cadastros, public.cadastros, int) to authenticated, service_role;
grant execute on function public._pontos(public.cadastros, public.cadastros) to authenticated, service_role;
grant execute on function public.analisar_par(uuid, uuid) to authenticated, service_role;
grant execute on function public.compatibilidades(integer) to authenticated, service_role;
