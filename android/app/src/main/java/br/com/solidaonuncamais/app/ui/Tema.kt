package br.com.solidaonuncamais.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.solidaonuncamais.app.R

/** Cores do site (as mesmas variáveis CSS de index.html). */
object Cores {
    val Vinho = Color(0xFF5A0F1F)
    val VinhoEscuro = Color(0xFF2D0710)
    val Vermelho = Color(0xFFC0152F)
    val VermelhoSuave = Color(0xFFE04A5F)
    val Dourado = Color(0xFFF2B823)
    val DouradoFundo = Color(0xFFC8900F)
    val Creme = Color(0xFFFDF7F1)
    val Rosado = Color(0xFFF8E6E3)
    val Tinta = Color(0xFF2B1418)
    val Suave = Color(0xFF7A5A5E)
    val Linha = Color(0xFFECD4CF)
    val Branco = Color.White
    val VerdeTag = Color(0xFFEAF5E8)
    val VerdeTexto = Color(0xFF2F6B2A)
    val AmareloTag = Color(0xFFFFF3D6)
    val AmareloTexto = Color(0xFF8A5B00)
    val Erro = Color(0xFFC62828)

    val BotaoPrincipal = Brush.linearGradient(listOf(Vermelho, Vinho))
    val Progresso = Brush.horizontalGradient(listOf(Vermelho, Dourado))
}

val Cormorant = FontFamily(
    Font(R.font.cormorant_semibold, FontWeight.SemiBold),
    Font(R.font.cormorant_bold, FontWeight.Bold),
    Font(R.font.cormorant_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
)

val Jost = FontFamily(
    Font(R.font.jost_regular, FontWeight.Normal),
    Font(R.font.jost_medium, FontWeight.Medium),
    Font(R.font.jost_semibold, FontWeight.SemiBold),
)

private val tipografia = Typography(
    displayMedium = TextStyle(fontFamily = Cormorant, fontWeight = FontWeight.SemiBold, fontSize = 44.sp, lineHeight = 48.sp, color = Cores.Vinho),
    headlineLarge = TextStyle(fontFamily = Cormorant, fontWeight = FontWeight.SemiBold, fontSize = 36.sp, lineHeight = 40.sp, color = Cores.Vinho),
    headlineMedium = TextStyle(fontFamily = Cormorant, fontWeight = FontWeight.SemiBold, fontSize = 30.sp, lineHeight = 34.sp, color = Cores.Vinho),
    titleLarge = TextStyle(fontFamily = Cormorant, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 28.sp, color = Cores.Vinho),
    bodyLarge = TextStyle(fontFamily = Jost, fontWeight = FontWeight.Normal, fontSize = 17.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontFamily = Jost, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 23.sp),
    bodySmall = TextStyle(fontFamily = Jost, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 19.sp),
    labelLarge = TextStyle(fontFamily = Jost, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    labelMedium = TextStyle(fontFamily = Jost, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelSmall = TextStyle(fontFamily = Jost, fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 2.5.sp),
)

private val esquema = lightColorScheme(
    primary = Cores.Vermelho,
    onPrimary = Cores.Branco,
    secondary = Cores.Dourado,
    onSecondary = Cores.Vinho,
    background = Cores.Creme,
    onBackground = Cores.Tinta,
    surface = Cores.Branco,
    onSurface = Cores.Tinta,
    onSurfaceVariant = Cores.Suave,
    outline = Cores.Linha,
    error = Cores.Erro,
)

@Composable
fun TemaSolidao(conteudo: @Composable () -> Unit) {
    MaterialTheme(colorScheme = esquema, typography = tipografia, content = conteudo)
}

/** Desliga as animações que repetem sem parar (usado só nas capturas de tela automáticas). */
val LocalAnimacoesContinuas = staticCompositionLocalOf { true }
