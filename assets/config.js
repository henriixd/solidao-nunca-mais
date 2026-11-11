// Conexão com o banco (Supabase). A chave "publishable" é pública por natureza:
// a segurança vem das regras (RLS) do banco — visitantes só conseguem CADASTRAR,
// e apenas admins conseguem ler os cadastros.
window.SNM_CONFIG = {
  supabaseUrl: 'https://fahybflzrylcbbavgczv.supabase.co',
  supabaseKey: 'sb_publishable_g1nK0kbs29eVABv7q2-Hzg_C3IE8No_'
};
