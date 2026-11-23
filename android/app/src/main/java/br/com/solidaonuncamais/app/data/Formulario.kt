package br.com.solidaonuncamais.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Versão dos termos aceitos. Deve ser igual a TERMOS_VERSAO do index.html. */
const val TERMOS_VERSAO = "2026-11-15"

enum class TipoTexto { NOME, EMAIL, TELEFONE, CIDADE }
enum class TipoAceite { MAIOR18, TERMOS, SENSIVEIS }

/** Cada pergunta do cadastro, no mesmo formato do array STEPS do site. */
sealed class Pergunta(val id: String, val rotulo: String, val obrigatoria: Boolean) {
    class Texto(id: String, rotulo: String, val tipo: TipoTexto, val exemplo: String, obrigatoria: Boolean = true) : Pergunta(id, rotulo, obrigatoria)
    class Nascimento(id: String, rotulo: String) : Pergunta(id, rotulo, true)
    class Estado(id: String, rotulo: String) : Pergunta(id, rotulo, true)
    class Unica(id: String, rotulo: String, val opcoes: List<String>, val cartoes: Boolean = false, obrigatoria: Boolean = false, val padrao: String? = null) : Pergunta(id, rotulo, obrigatoria)
    class Multipla(id: String, rotulo: String, val opcoes: List<String>, val max: Int? = null, val tantoFaz: Boolean = false, val dica: String? = null, obrigatoria: Boolean = false) : Pergunta(id, rotulo, obrigatoria)
    class Deslizante(id: String, rotulo: String, val min: Int, val max: Int, val padrao: Int, val unidade: String) : Pergunta(id, rotulo, false)
    class Faixa(id: String, rotulo: String, val min: Int, val max: Int, val padrao: List<Int>, val unidade: String, val qualquer: Boolean = false) : Pergunta(id, rotulo, false)
    class TextoLongo(id: String, rotulo: String, val exemplo: String) : Pergunta(id, rotulo, false)
    class Aceite(id: String, val tipo: TipoAceite) : Pergunta(id, "", true)
}

class Etapa(val emoji: String, val titulo: String, val descricao: String, val perguntas: List<Pergunta>)

val ETAPAS = listOf(
    Etapa("🌹", "Vamos começar", "Seus dados ficam em sigilo. Só usamos para avisar quando houver alguém compatível.", listOf(
        Pergunta.Texto("nome", "Seu nome", TipoTexto.NOME, "Como você se chama?"),
        Pergunta.Unica("genero", "Você é", Opcoes.genero, cartoes = true, obrigatoria = true),
        Pergunta.Nascimento("nascimento", "Data de nascimento"),
        Pergunta.Texto("whatsapp", "WhatsApp", TipoTexto.TELEFONE, "(51) 99999-9999"),
        Pergunta.Texto("email", "E-mail", TipoTexto.EMAIL, "voce@email.com"),
        Pergunta.Texto("cidade", "Cidade", TipoTexto.CIDADE, "Porto Alegre"),
        Pergunta.Estado("uf", "Estado"),
    )),
    Etapa("✨", "Como você é", "Toque nas opções que mais combinam com você.", listOf(
        Pergunta.Deslizante("altura", "Sua altura", 140, 210, 168, " cm"),
        Pergunta.Unica("corpo", "Tipo físico", Opcoes.corpo, obrigatoria = true),
        Pergunta.Unica("pele", "Cor da pele", Opcoes.pele),
        Pergunta.Unica("olhos", "Olhos", Opcoes.olhos),
        Pergunta.Unica("cabelo", "Cabelo", Opcoes.cabelo),
    )),
    Etapa("☕", "Seu estilo de vida", "Como é o seu dia a dia?", listOf(
        Pergunta.Unica("estado_civil", "Estado civil", Opcoes.estadoCivil, obrigatoria = true),
        Pergunta.Unica("filhos", "Filhos", Opcoes.filhos, obrigatoria = true),
        Pergunta.Unica("quer_filhos", "Quer ter (mais) filhos?", Opcoes.querFilhos, cartoes = true),
        Pergunta.Unica("fuma", "Você fuma?", Opcoes.fuma, cartoes = true, obrigatoria = true),
        Pergunta.Unica("bebe", "Você bebe?", Opcoes.bebe, cartoes = true, obrigatoria = true),
        Pergunta.Unica("escolaridade", "Escolaridade", Opcoes.escolaridade),
        Pergunta.Unica("atividade", "Atividade física", Opcoes.atividade),
        Pergunta.Unica("pets", "Animais de estimação", Opcoes.pets),
    )),
    Etapa("✨", "Seus valores e seu jeito", "É aqui que o cruzamento de dados mais aprende sobre você.", listOf(
        Pergunta.Unica("religiao", "Religião", Opcoes.religiao, obrigatoria = true),
        Pergunta.Unica("religiao_importancia", "A fé na sua vida é…", Opcoes.religiaoImportancia),
        Pergunta.Unica("politica", "Posição política", Opcoes.politica, obrigatoria = true),
        Pergunta.Multipla("personalidade", "Sua personalidade", Opcoes.personalidade, max = 5, dica = "escolha até 5", obrigatoria = true),
        Pergunta.Multipla("interesses", "O que você gosta de fazer", Opcoes.interesses, max = 6, dica = "escolha até 6"),
    )),
    Etapa("💘", "Quem você procura", "Agora conte como é a pessoa que você quer encontrar.", listOf(
        Pergunta.Unica("procura_genero", "Procuro", Opcoes.procuraGenero, cartoes = true, obrigatoria = true),
        Pergunta.Unica("intencao", "Para um relacionamento", Opcoes.intencao, cartoes = true, obrigatoria = true),
        Pergunta.Faixa("idade", "Com idade entre", 18, 80, listOf(25, 45), " anos"),
        Pergunta.Unica("distancia", "Morando", Opcoes.distancia, cartoes = true, padrao = "Mesmo estado"),
        Pergunta.Faixa("pref_altura", "Altura", 140, 210, listOf(140, 210), " cm", qualquer = true),
        Pergunta.Multipla("pref_corpo", "Tipo físico", Opcoes.corpo, tantoFaz = true),
        Pergunta.Multipla("deseja", "Que seja", Opcoes.personalidade, max = 5, dica = "escolha até 5"),
    )),
    Etapa("💎", "O que não pode faltar", "Marque o que você aceita. Se não fizer diferença, deixe em \"Tanto faz\".", listOf(
        Pergunta.Multipla("pref_religiao", "Religião da pessoa", Opcoes.religiao, tantoFaz = true),
        Pergunta.Multipla("pref_politica", "Posição política da pessoa", Opcoes.politica, tantoFaz = true),
        Pergunta.Multipla("pref_fuma", "Sobre fumar, aceito quem", Opcoes.fuma, tantoFaz = true),
        Pergunta.Multipla("pref_bebe", "Sobre beber, aceito quem", Opcoes.bebe, tantoFaz = true),
        Pergunta.Unica("pref_filhos", "Sobre filhos", Opcoes.prefFilhos, padrao = "Aceito quem tem filhos"),
        Pergunta.TextoLongo("sobre", "Conte algo sobre você", "O que te faz feliz, seus sonhos, um programa perfeito…"),
        Pergunta.TextoLongo("pessoa_ideal", "Descreva a pessoa dos seus sonhos", "Como ela é, o que vocês fariam juntos…"),
        Pergunta.Aceite("maior18", TipoAceite.MAIOR18),
        Pergunta.Aceite("termos", TipoAceite.TERMOS),
        Pergunta.Aceite("sensiveis", TipoAceite.SENSIVEIS),
    )),
)

val INCENTIVOS = listOf("Ótimo começo! 💛", "Está ficando lindo! 🌹", "Metade do caminho! ✨", "Falta pouco! 💘", "Última etapa! 💎")

private val todasPerguntas = ETAPAS.flatMap { it.perguntas }
fun pergunta(id: String): Pergunta = todasPerguntas.first { it.id == id }

/** Guarda as respostas enquanto a pessoa preenche (sobrevive a girar a tela). */
class CadastroViewModel : ViewModel() {
    val respostas = mutableStateMapOf<String, Any>()
    var etapa by mutableIntStateOf(0)

    init { reiniciar() }

    fun reiniciar() {
        respostas.clear()
        etapa = 0
        todasPerguntas.forEach { p ->
            when (p) {
                is Pergunta.Multipla -> respostas[p.id] = emptyList<String>()
                is Pergunta.Deslizante -> respostas[p.id] = p.padrao
                is Pergunta.Faixa -> respostas[p.id] = p.padrao
                is Pergunta.Aceite -> respostas[p.id] = false
                is Pergunta.Unica -> p.padrao?.let { respostas[p.id] = it }
                else -> Unit
            }
        }
    }

    fun texto(id: String): String = respostas[id] as? String ?: ""
    @Suppress("UNCHECKED_CAST")
    fun lista(id: String): List<String> = respostas[id] as? List<String> ?: emptyList()
    fun numero(id: String): Int = respostas[id] as Int
    @Suppress("UNCHECKED_CAST")
    fun faixa(id: String): List<Int> = respostas[id] as List<Int>
    fun marcado(id: String): Boolean = respostas[id] as? Boolean ?: false

    fun primeiroNome(): String = texto("nome").trim().split(Regex("\\s+")).firstOrNull().orEmpty()

    /** Confere a etapa atual. Devolve os ids com problema e, se houver, um aviso. */
    fun validar(indice: Int): Pair<List<String>, String?> {
        val ruins = mutableListOf<String>()
        var aviso: String? = null
        ETAPAS[indice].perguntas.forEach { p ->
            var ruim = p.obrigatoria && when (p) {
                is Pergunta.Multipla -> lista(p.id).isEmpty()
                is Pergunta.Aceite -> !marcado(p.id)
                else -> texto(p.id).isBlank()
            }
            if (!ruim && p is Pergunta.Texto && p.tipo == TipoTexto.EMAIL &&
                !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(texto(p.id).trim())) ruim = true
            if (!ruim && p is Pergunta.Texto && p.tipo == TipoTexto.TELEFONE &&
                texto(p.id).filter { it.isDigit() }.length < 10) ruim = true
            if (!ruim && p is Pergunta.Nascimento && idade(texto(p.id)) < 18) {
                ruim = true
                aviso = "O cadastro é permitido apenas para maiores de 18 anos."
            }
            if (ruim) ruins += p.id
        }
        return ruins to aviso
    }

    /** Monta o JSON com os mesmos campos e regras que o site envia. */
    fun montarEnvio(): JSONObject {
        fun s(id: String): Any = texto(id).trim().ifEmpty { null } ?: JSONObject.NULL
        fun arr(id: String) = JSONArray(lista(id))
        val idade = faixa("idade")
        val altura = faixa("pref_altura")
        val alturaLivre = altura[0] == 140 && altura[1] == 210
        val agora = isoAgora()
        return JSONObject().apply {
            put("nome", s("nome")); put("email", s("email")); put("whatsapp", s("whatsapp"))
            put("nascimento", s("nascimento")); put("genero", s("genero"))
            put("cidade", s("cidade")); put("uf", s("uf")); put("altura", numero("altura"))
            put("corpo", s("corpo")); put("pele", s("pele")); put("olhos", s("olhos")); put("cabelo", s("cabelo"))
            put("estado_civil", s("estado_civil")); put("filhos", s("filhos")); put("quer_filhos", s("quer_filhos"))
            put("escolaridade", s("escolaridade")); put("fuma", s("fuma")); put("bebe", s("bebe"))
            put("atividade", s("atividade")); put("pets", s("pets"))
            put("religiao", s("religiao")); put("religiao_importancia", s("religiao_importancia")); put("politica", s("politica"))
            put("personalidade", arr("personalidade")); put("interesses", arr("interesses"))
            put("procura_genero", s("procura_genero")); put("intencao", s("intencao"))
            put("idade_min", idade[0]); put("idade_max", if (idade[1] == 80) 120 else idade[1])
            put("distancia", s("distancia"))
            put("pref_altura_min", if (alturaLivre) JSONObject.NULL else altura[0])
            put("pref_altura_max", if (alturaLivre) JSONObject.NULL else altura[1])
            put("pref_corpo", arr("pref_corpo")); put("deseja", arr("deseja"))
            put("pref_religiao", arr("pref_religiao")); put("pref_politica", arr("pref_politica"))
            put("pref_fuma", arr("pref_fuma")); put("pref_bebe", arr("pref_bebe")); put("pref_filhos", s("pref_filhos"))
            put("sobre", s("sobre")); put("pessoa_ideal", s("pessoa_ideal"))
            // Registro do consentimento (o banco grava a data/hora oficial no momento do envio).
            put("termos_versao", TERMOS_VERSAO)
            put("aceite_termos_em", if (marcado("termos")) agora else JSONObject.NULL)
            put("consentimento_sensiveis_em", if (marcado("sensiveis")) agora else JSONObject.NULL)
        }
    }
}

/** Idade completa a partir de "aaaa-mm-dd". */
fun idade(data: String): Int {
    val partes = data.split("-").mapNotNull { it.toIntOrNull() }
    if (partes.size != 3) return 0
    val hoje = Calendar.getInstance()
    var anos = hoje.get(Calendar.YEAR) - partes[0]
    val mes = hoje.get(Calendar.MONTH) + 1
    val dia = hoje.get(Calendar.DAY_OF_MONTH)
    if (mes < partes[1] || (mes == partes[1] && dia < partes[2])) anos--
    return anos
}

/** Converte a data escolhida no calendário (meia-noite UTC) para "aaaa-mm-dd". */
fun dataDeMillis(millis: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date(millis))

fun dataParaExibir(data: String): String {
    val p = data.split("-")
    return if (p.size == 3) "${p[2]}/${p[1]}/${p[0]}" else ""
}

private fun isoAgora(): String =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())
