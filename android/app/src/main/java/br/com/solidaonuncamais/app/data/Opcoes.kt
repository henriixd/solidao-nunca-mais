package br.com.solidaonuncamais.app.data

/**
 * Opções do cadastro. Cópia fiel de assets/campos.js do site:
 * os textos precisam ser IGUAIS aos gravados no banco, então altere os dois juntos.
 */
object Opcoes {
    val uf = listOf("AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG","PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO")
    val genero = listOf("Homem", "Mulher", "Prefiro não dizer")
    val procuraGenero = listOf("Homem", "Mulher", "Tanto faz")
    val corpo = listOf("Magro(a)", "Atlético(a)", "Mediano(a)", "Com curvas", "Acima do peso")
    val pele = listOf("Branca", "Parda", "Negra", "Amarela", "Indígena")
    val olhos = listOf("Castanhos", "Pretos", "Mel", "Verdes", "Azuis")
    val cabelo = listOf("Preto", "Castanho", "Loiro", "Ruivo", "Grisalho", "Careca / raspado")
    val estadoCivil = listOf("Solteiro(a)", "Divorciado(a)", "Separado(a)", "Viúvo(a)")
    val filhos = listOf("Não tenho", "Tenho, moram comigo", "Tenho, não moram comigo")
    val querFilhos = listOf("Quero ter", "Não quero", "Ainda não sei")
    val escolaridade = listOf("Fundamental", "Médio", "Técnico", "Superior", "Pós-graduação")
    val fuma = listOf("Não fumo", "Às vezes", "Fumo")
    val bebe = listOf("Não bebo", "Socialmente", "Com frequência")
    val atividade = listOf("Quase nunca", "Às vezes", "Regularmente", "Todo dia")
    val pets = listOf("Tenho pets", "Gosto, mas não tenho", "Não gosto de animais")
    val religiao = listOf("Católica", "Evangélica", "Espírita", "Umbanda / Candomblé", "Outra religião", "Espiritualizado(a)", "Sem religião / Ateu")
    val religiaoImportancia = listOf("Muito importante", "Importante", "Pouco importante")
    val politica = listOf("Esquerda", "Centro-esquerda", "Centro", "Centro-direita", "Direita", "Não me interesso por política")
    val personalidade = listOf("Romântico(a)", "Carinhoso(a)", "Bem-humorado(a)", "Calmo(a)", "Extrovertido(a)", "Tímido(a)", "Caseiro(a)", "Aventureiro(a)", "Trabalhador(a)", "Família em 1º lugar", "Intelectual", "Festeiro(a)")
    val interesses = listOf("Viajar", "Cinema e séries", "Música", "Dançar", "Cozinhar", "Ler", "Esportes", "Natureza e trilhas", "Igreja / fé", "Games", "Sair à noite", "Churrasco com amigos")
    val intencao = listOf("Namoro sério", "Casamento", "Amizade que pode virar algo mais")
    val distancia = listOf("Mesma cidade", "Mesmo estado", "Qualquer lugar do Brasil")
    val prefFilhos = listOf("Aceito quem tem filhos", "Prefiro quem não tem filhos")

    val icones = mapOf(
        "Homem" to "👨", "Mulher" to "👩", "Prefiro não dizer" to "🌈", "Tanto faz" to "💞",
        "Namoro sério" to "💑", "Casamento" to "💍", "Amizade que pode virar algo mais" to "🤝",
        "Mesma cidade" to "🏙️", "Mesmo estado" to "🗺️", "Qualquer lugar do Brasil" to "🇧🇷",
        "Não fumo" to "🚭", "Às vezes" to "🙂", "Fumo" to "🚬",
        "Não bebo" to "🥤", "Socialmente" to "🥂", "Com frequência" to "🍻",
        "Quero ter" to "👶", "Não quero" to "✋", "Ainda não sei" to "🤔",
    )
}

/** Dados de contato e endereços, os mesmos do site. */
object Contato {
    const val SITE = "https://www.solidaonuncamais.com.br"
    const val TERMOS = "$SITE/privacidade.html#termos"
    const val PRIVACIDADE = "$SITE/privacidade.html#privacidade"
    const val EMAIL = "contato@solidaonuncamais.com.br"
    const val WHATSAPP_NUMERO = "5551920017053"
    const val WHATSAPP_TEXTO = "(51) 92001-7053"
}
