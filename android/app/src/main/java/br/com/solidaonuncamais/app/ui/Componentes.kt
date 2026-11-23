package br.com.solidaonuncamais.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.solidaonuncamais.app.data.Contato

val Pilula = RoundedCornerShape(999.dp)

/** Botão principal: degradê vermelho → vinho, como o .btn-primary do site. */
@Composable
fun BotaoPrincipal(
    texto: String,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    dourado: Boolean = false,
) {
    val fundo = if (dourado) Modifier.background(Cores.Dourado, Pilula) else Modifier.background(Cores.BotaoPrincipal, Pilula)
    Box(
        modifier
            .defaultMinSize(minHeight = 52.dp)
            .shadow(if (dourado) 0.dp else 10.dp, Pilula, ambientColor = Cores.Vermelho, spotColor = Cores.Vermelho)
            .clip(Pilula)
            .then(fundo)
            .alpha(if (habilitado) 1f else 0.6f)
            .clickable(enabled = habilitado, role = Role.Button, onClick = aoClicar)
            .padding(horizontal = 26.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge, color = if (dourado) Cores.Vinho else Color.White, textAlign = TextAlign.Center)
    }
}

/** Botão com contorno vinho, como o .btn-ghost do site. */
@Composable
fun BotaoContorno(texto: String, aoClicar: () -> Unit, modifier: Modifier = Modifier, cor: Color = Cores.Vinho) {
    Box(
        modifier
            .defaultMinSize(minHeight = 52.dp)
            .clip(Pilula)
            .border(1.5.dp, cor, Pilula)
            .clickable(role = Role.Button, onClick = aoClicar)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge, color = cor, textAlign = TextAlign.Center)
    }
}

/** Texto pequeno em caixa alta acima dos títulos (.eyebrow). */
@Composable
fun Sobretitulo(texto: String, modifier: Modifier = Modifier, cor: Color = Cores.Vermelho, centralizado: Boolean = false) {
    Text(
        texto.uppercase(),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = cor,
        textAlign = if (centralizado) TextAlign.Center else TextAlign.Start,
    )
}

/** Etiqueta arredondada ("Gratuito", "R$ 70 · Pix ou cartão"). */
@Composable
fun Etiqueta(texto: String, paga: Boolean = false) {
    Text(
        texto,
        modifier = Modifier
            .background(if (paga) Cores.AmareloTag else Cores.VerdeTag, Pilula)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = if (paga) Cores.AmareloTexto else Cores.VerdeTexto,
    )
}

fun abrirLink(contexto: Context, url: String) {
    try {
        contexto.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
        Toast.makeText(contexto, "Não foi possível abrir o link.", Toast.LENGTH_SHORT).show()
    }
}

fun abrirWhatsApp(contexto: Context, mensagem: String? = null) {
    val texto = mensagem?.let { "?text=" + Uri.encode(it) }.orEmpty()
    abrirLink(contexto, "https://wa.me/${Contato.WHATSAPP_NUMERO}$texto")
}

fun enviarEmail(contexto: Context, assunto: String = "", corpo: String = "") {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(Contato.EMAIL))
        putExtra(Intent.EXTRA_SUBJECT, assunto)
        putExtra(Intent.EXTRA_TEXT, corpo)
    }
    try {
        contexto.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(contexto, "Nenhum app de e-mail encontrado. Escreva para ${Contato.EMAIL}", Toast.LENGTH_LONG).show()
    }
}
