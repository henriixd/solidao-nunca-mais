// Opções do cadastro — usadas pelo site (formulário) e pelo painel admin.
// Os textos precisam ser IGUAIS aos gravados no banco, então altere com cuidado.
window.SNM_OPCOES = {
  uf: ['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'],
  genero: ['Homem', 'Mulher', 'Prefiro não dizer'],
  procura_genero: ['Homem', 'Mulher', 'Tanto faz'],
  corpo: ['Magro(a)', 'Atlético(a)', 'Mediano(a)', 'Com curvas', 'Acima do peso'],
  pele: ['Branca', 'Parda', 'Negra', 'Amarela', 'Indígena'],
  olhos: ['Castanhos', 'Pretos', 'Mel', 'Verdes', 'Azuis'],
  cabelo: ['Preto', 'Castanho', 'Loiro', 'Ruivo', 'Grisalho', 'Careca / raspado'],
  estado_civil: ['Solteiro(a)', 'Divorciado(a)', 'Separado(a)', 'Viúvo(a)'],
  filhos: ['Não tenho', 'Tenho, moram comigo', 'Tenho, não moram comigo'],
  quer_filhos: ['Quero ter', 'Não quero', 'Ainda não sei'],
  escolaridade: ['Fundamental', 'Médio', 'Técnico', 'Superior', 'Pós-graduação'],
  fuma: ['Não fumo', 'Às vezes', 'Fumo'],
  bebe: ['Não bebo', 'Socialmente', 'Com frequência'],
  atividade: ['Quase nunca', 'Às vezes', 'Regularmente', 'Todo dia'],
  pets: ['Tenho pets', 'Gosto, mas não tenho', 'Não gosto de animais'],
  religiao: ['Católica', 'Evangélica', 'Espírita', 'Umbanda / Candomblé', 'Outra religião', 'Espiritualizado(a)', 'Sem religião / Ateu'],
  religiao_importancia: ['Muito importante', 'Importante', 'Pouco importante'],
  politica: ['Esquerda', 'Centro-esquerda', 'Centro', 'Centro-direita', 'Direita', 'Não me interesso por política'],
  personalidade: ['Romântico(a)', 'Carinhoso(a)', 'Bem-humorado(a)', 'Calmo(a)', 'Extrovertido(a)', 'Tímido(a)', 'Caseiro(a)', 'Aventureiro(a)', 'Trabalhador(a)', 'Família em 1º lugar', 'Intelectual', 'Festeiro(a)'],
  interesses: ['Viajar', 'Cinema e séries', 'Música', 'Dançar', 'Cozinhar', 'Ler', 'Esportes', 'Natureza e trilhas', 'Igreja / fé', 'Games', 'Sair à noite', 'Churrasco com amigos'],
  intencao: ['Namoro sério', 'Casamento', 'Amizade que pode virar algo mais'],
  distancia: ['Mesma cidade', 'Mesmo estado', 'Qualquer lugar do Brasil'],
  pref_filhos: ['Aceito quem tem filhos', 'Prefiro quem não tem filhos'],
  icones: {
    'Homem': '👨', 'Mulher': '👩', 'Prefiro não dizer': '🌈', 'Tanto faz': '💞',
    'Namoro sério': '💑', 'Casamento': '💍', 'Amizade que pode virar algo mais': '🤝',
    'Mesma cidade': '🏙️', 'Mesmo estado': '🗺️', 'Qualquer lugar do Brasil': '🇧🇷',
    'Não fumo': '🚭', 'Às vezes': '🙂', 'Fumo': '🚬', 'Não bebo': '🥤', 'Socialmente': '🥂', 'Com frequência': '🍻',
    'Quero ter': '👶', 'Não quero': '✋', 'Ainda não sei': '🤔'
  }
};

// Rótulos amigáveis de cada campo (usados no painel)
window.SNM_ROTULOS = {
  altura: 'Altura', corpo: 'Tipo físico', pele: 'Cor da pele', olhos: 'Olhos', cabelo: 'Cabelo',
  estado_civil: 'Estado civil', filhos: 'Filhos', quer_filhos: 'Quer ter filhos', escolaridade: 'Escolaridade',
  fuma: 'Fuma', bebe: 'Bebe', atividade: 'Atividade física', pets: 'Animais',
  religiao: 'Religião', religiao_importancia: 'Importância da fé', politica: 'Política',
  personalidade: 'Personalidade', interesses: 'Interesses',
  procura_genero: 'Procura', intencao: 'Intenção', distancia: 'Distância', deseja: 'Personalidade desejada',
  pref_corpo: 'Tipo físico', pref_religiao: 'Religião aceita', pref_politica: 'Política aceita',
  pref_fuma: 'Sobre fumar', pref_bebe: 'Sobre beber', pref_filhos: 'Sobre filhos'
};
