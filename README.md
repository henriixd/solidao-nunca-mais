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
                  busca manual para amigos e registro de pagamentos
assets/config.js  URL e chave pública do Supabase
assets/           Imagens das rosas e logo
```

## Banco de dados (Supabase: projeto `solidao-nunca-mais`)

- `cadastros`: todos os cadastros do site. Visitantes só podem **inserir**; apenas admins leem, editam e apagam.
- `pagamentos`: pagamentos registrados por cadastro (a coluna "Já pagou" do painel vem daqui).
- `admins`: e-mails com acesso ao painel. Para adicionar um admin, crie o usuário em
  *Authentication → Users* no Supabase e rode: `insert into admins (email) values ('email@exemplo.com');`
- `compatibilidades()`: pares compatíveis nos dois sentidos (gênero, faixa de idade, distância) com pontuação de afinidade.
- `buscar_para_amigo(...)`: busca manual no banco para alguém que não está cadastrado.

Para ver, basta abrir o `index.html` no navegador. Também funciona direto no GitHub Pages.

## Próximos passos

- [x] Salvar os cadastros no Supabase
- [x] Cruzamento de dados
- [x] Painel admin
- [ ] Pagamento via Pix e cartão (ex.: Mercado Pago)
- [ ] Liberação do contato / agendamento do encontro após ambos pagarem
- [ ] Página de política de privacidade (LGPD) e termos de uso
