package br.com.solidaonuncamais.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Envio do cadastro para o Supabase, exatamente como o site faz
 * (POST em /rest/v1/cadastros com a chave pública). A segurança vem das
 * regras do banco: visitantes só conseguem inserir, nunca ler.
 */
object CadastroApi {
    // Mesmos valores de assets/config.js do site.
    private const val SUPABASE_URL = "https://fahybflzrylcbbavgczv.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_g1nK0kbs29eVABv7q2-Hzg_C3IE8No_"

    /** Resultado do envio: null = deu certo; texto = mensagem de erro para a pessoa. */
    suspend fun enviar(dados: JSONObject): String? = withContext(Dispatchers.IO) {
        var conexao: HttpURLConnection? = null
        try {
            conexao = (URL("$SUPABASE_URL/rest/v1/cadastros").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 20_000
                doOutput = true
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Prefer", "return=minimal")
            }
            conexao.outputStream.use { it.write(dados.toString().toByteArray(Charsets.UTF_8)) }
            val codigo = conexao.responseCode
            if (codigo in 200..299) return@withContext null
            val corpo = conexao.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            traduzirErro(corpo)
        } catch (e: Exception) {
            "Não foi possível enviar seu cadastro agora. Verifique sua conexão e tente novamente."
        } finally {
            conexao?.disconnect()
        }
    }

    /** Mesmas mensagens do site para os erros do gatilho proteger_cadastro. */
    private fun traduzirErro(corpo: String): String = when {
        "SNM:duplicado" in corpo -> "Já existe um cadastro com este e-mail ou WhatsApp. Se precisar alterar seus dados, fale com a nossa equipe."
        "SNM:limite" in corpo -> "Recebemos muitos cadastros agora há pouco. Por favor, tente de novo em alguns minutos."
        "SNM:termos" in corpo -> "Para concluir, é preciso aceitar os Termos de Uso e autorizar o uso dos dados sensíveis."
        "SNM:grande" in corpo -> "Algum campo ficou grande demais. Revise os textos e tente novamente."
        else -> "Não foi possível enviar seu cadastro agora. Verifique sua conexão e tente novamente."
    }
}
