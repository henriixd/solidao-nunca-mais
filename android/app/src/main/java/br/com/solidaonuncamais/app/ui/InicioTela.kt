package br.com.solidaonuncamais.app.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.solidaonuncamais.app.R
import br.com.solidaonuncamais.app.data.Contato
import kotlinx.coroutines.launch
import java.util.Calendar

private val Cartao = RoundedCornerShape(22.dp)

/** Tela inicial: a mesma sequência de seções do site. */
@Composable
fun InicioTela(aoCadastrar: () -> Unit) {
    val lista = rememberLazyListState()
    val escopo = rememberCoroutineScope()
    var pedirExclusao by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Cores.Creme)) {
        Cabecalho(aoCadastrar)
        LazyColumn(state = lista, modifier = Modifier.fillMaxSize()) {
            item { Destaque(aoCadastrar, aoComoFunciona = { escopo.launch { lista.animateScrollToItem(1) } }) }
            item { ComoFunciona() }
            item { Simbolo() }
            item { Valores(aoCadastrar) }
            item { Duvidas() }
            item { ChamadaFinal(aoCadastrar) }
            item { Rodape(aoExcluir = { pedirExclusao = true }) }
        }
    }

    if (pedirExclusao) DialogoExclusao(aoFechar = { pedirExclusao = false })
}

@Composable
private fun Cabecalho(aoCadastrar: () -> Unit) {
    Surface(color = Cores.Creme, shadowElevation = 1.dp) {
        Row(
            Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Logo(44)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Solidão Nunca Mais", fontFamily = Cormorant, fontWeight = FontWeight.Bold, fontSize = 21.sp, color = Cores.Vinho, lineHeight = 22.sp)
                Text("AGÊNCIA DE RELACIONAMENTOS", fontFamily = Jost, fontSize = 9.sp, letterSpacing = 1.6.sp, color = Cores.Suave)
            }
            Text(
                "Cadastre-se",
                modifier = Modifier.clip(Pilula).background(Cores.BotaoPrincipal).clickable(role = Role.Button, onClick = aoCadastrar)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.labelMedium, color = Color.White,
            )
        }
    }
}

@Composable
fun Logo(tamanho: Int) {
    Image(
        painterResource(R.drawable.logo_rosas), contentDescription = "Logo Solidão Nunca Mais",
        modifier = Modifier.size(tamanho.dp).border(3.dp, Cores.Dourado, CircleShape).padding(2.dp).border(2.dp, Color.White, CircleShape).clip(CircleShape),
    )
}

@Composable
private fun Destaque(aoCadastrar: () -> Unit, aoComoFunciona: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(
            Brush.radialGradient(listOf(Cores.Dourado.copy(alpha = 0.16f), Color.Transparent), radius = 900f, center = androidx.compose.ui.geometry.Offset(900f, 80f))
        ).padding(horizontal = 22.dp, vertical = 28.dp),
    ) {
        RosasBalancando(Modifier.fillMaxWidth(0.82f).align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(24.dp))
        Sobretitulo("Agência de relacionamentos")
        Spacer(Modifier.height(8.dp))
        Text(
            buildAnnotatedString {
                append("Alguém especial\nestá ")
                withStyle(SpanStyle(color = Cores.Vermelho, fontStyle = FontStyle.Italic)) { append("esperando") }
                append("\npor você.")
            },
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "Conte quem você é e quem você sonha encontrar. Nosso sistema compara os dados e avisa quando existir alguém compatível com você. Cadastro 100% gratuito.",
            style = MaterialTheme.typography.bodyLarge, color = Cores.Suave,
        )
        Spacer(Modifier.height(22.dp))
        BotaoPrincipal("Quero me cadastrar ♥", aoCadastrar, Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        BotaoContorno("Como funciona", aoComoFunciona, Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            Selo("Grátis", "cadastro e busca")
            Selo("Sigilo", "seus dados estão protegidos")
        }
    }
}

@Composable
private fun Selo(titulo: String, texto: String) {
    Column {
        Text(titulo, fontFamily = Cormorant, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = Cores.Vinho, lineHeight = 26.sp)
        Text(texto, style = MaterialTheme.typography.bodySmall, color = Cores.Suave)
    }
}

/** As duas rosas balançando, com as legendas, como no topo do site. */
@Composable
private fun RosasBalancando(modifier: Modifier = Modifier) {
    val t = rememberInfiniteTransition(label = "rosas")
    val vai by t.animateFloat(0f, 1f, infiniteRepeatable(tween(3500), RepeatMode.Reverse), label = "balanço")
    val giro by t.animateFloat(0f, 360f, infiniteRepeatable(tween(60_000, easing = LinearEasing)), label = "anel")
    Box(modifier.aspectRatio(1f)) {
        Box(Modifier.fillMaxSize().padding(16.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Color.White, Cores.Rosado, Color.Transparent))))
        Box(Modifier.fillMaxSize().graphicsLayer { rotationZ = giro }.border(1.dp, Cores.Vermelho.copy(alpha = 0.25f), CircleShape))
        Image(
            painterResource(R.drawable.rosa_amarela), contentDescription = "Rosa amarela, simbolizando a fidelidade",
            modifier = Modifier.fillMaxWidth(0.72f).align(Alignment.BottomCenter).padding(bottom = 8.dp)
                .graphicsLayer { translationX = size.width * 0.12f; transformOrigin = TransformOrigin(0.4f, 1f); rotationZ = 8f + 4f * vai },
        )
        Image(
            painterResource(R.drawable.rosa_vermelha), contentDescription = "Rosa vermelha, simbolizando o amor",
            modifier = Modifier.fillMaxWidth(0.45f).align(Alignment.BottomCenter).padding(bottom = 8.dp)
                .graphicsLayer { translationX = -size.width * 0.28f; transformOrigin = TransformOrigin(0.75f, 1f); rotationZ = -11f + 4f * vai },
        )
        Legenda("Rosa vermelha · Amor", Cores.Vermelho, Modifier.align(Alignment.TopStart).padding(top = 30.dp))
        Legenda("Rosa amarela · Fidelidade", Cores.Dourado, Modifier.align(Alignment.BottomEnd).padding(bottom = 50.dp))
    }
}

@Composable
private fun Legenda(texto: String, cor: Color, modifier: Modifier) {
    Row(
        modifier.shadow(8.dp, RoundedCornerShape(14.dp)).background(Color.White, RoundedCornerShape(14.dp)).padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(10.dp).background(cor, CircleShape))
        Spacer(Modifier.width(7.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun CabecalhoSecao(sobre: String, titulo: String, texto: String? = null, claro: Boolean = false) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Sobretitulo(sobre, cor = if (claro) Cores.Dourado else Cores.Vermelho, centralizado = true)
        Spacer(Modifier.height(8.dp))
        Text(
            titulo, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center,
            color = if (claro) Color.White else Cores.Vinho, modifier = Modifier.semantics { heading() },
        )
        if (texto != null) {
            Spacer(Modifier.height(10.dp))
            Text(texto, style = MaterialTheme.typography.bodyMedium, color = if (claro) Color(0xFFF1D9DC) else Cores.Suave, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(26.dp))
    }
}

@Composable
private fun ComoFunciona() {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 48.dp)) {
        CabecalhoSecao("Simples e discreto", "Do cadastro ao primeiro encontro", "Você só paga quando encontramos alguém compatível e decide que quer conhecer essa pessoa.")
        val passos = listOf(
            Triple("Conte quem você é", "Faça seu cadastro com seu jeito de ser, seus gostos, valores e estilo de vida.", "Gratuito"),
            Triple("Diga quem procura", "Descreva a pessoa dos seus sonhos: idade, personalidade, interesses e intenções.", "Gratuito"),
            Triple("Cruzamento de dados", "Comparamos como você é com o que outras pessoas desejam, e vice-versa. Avisamos quando há compatibilidade.", "Gratuito"),
            Triple("Conheça seu par", "Quer conhecer? Pague a taxa. Quando os dois pagarem, liberamos o contato ou marcamos um encontro às cegas.", "R$ 70 · Pix ou cartão"),
        )
        passos.forEachIndexed { i, (titulo, texto, tag) ->
            Box(Modifier.fillMaxWidth().padding(bottom = 14.dp).background(Color.White, Cartao).border(1.dp, Cores.Linha, Cartao).padding(22.dp)) {
                Text("0${i + 1}", fontFamily = Cormorant, fontWeight = FontWeight.Bold, fontSize = 44.sp, color = Cores.Rosado, modifier = Modifier.align(Alignment.TopEnd))
                Column {
                    Text(titulo, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(6.dp))
                    Text(texto, style = MaterialTheme.typography.bodyMedium, color = Cores.Suave)
                    Spacer(Modifier.height(12.dp))
                    Etiqueta(tag, paga = i == 3)
                }
            }
        }
    }
}

@Composable
private fun Simbolo() {
    Column(
        Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Cores.Vinho, Cores.VinhoEscuro))).padding(horizontal = 20.dp, vertical = 52.dp),
    ) {
        CabecalhoSecao(
            "Nosso símbolo", "Duas rosas, um encontro",
            "Nosso logo une duas rosas que crescem lado a lado: a vermelha do amor e a amarela da fidelidade. Juntas, representam o que todo relacionamento verdadeiro precisa. Aqui o amor é para todos: casais de homem e mulher, de dois homens ou de duas mulheres.",
            claro = true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CartaoRosa(R.drawable.rosa_vermelha, "Amor", "Simboliza o amor: a paixão, o carinho e a força do sentimento.", Color(0xFFFF7A8C), Modifier.weight(1f))
            CartaoRosa(R.drawable.rosa_amarela, "Fidelidade", "Simboliza a fidelidade: a lealdade, a confiança e a amizade que sustentam uma relação.", Cores.Dourado, Modifier.weight(1f))
        }
    }
}

@Composable
private fun CartaoRosa(imagem: Int, titulo: String, texto: String, cor: Color, modifier: Modifier) {
    Column(
        modifier.border(1.dp, Color.White.copy(alpha = 0.15f), Cartao).background(Color.White.copy(alpha = 0.04f), Cartao).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painterResource(imagem), contentDescription = null, modifier = Modifier.height(120.dp))
        Spacer(Modifier.height(8.dp))
        Text(titulo, fontFamily = Cormorant, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, color = cor)
        Spacer(Modifier.height(4.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall, color = Color(0xFFF1D9DC), textAlign = TextAlign.Center)
    }
}

@Composable
private fun Valores(aoCadastrar: () -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 52.dp)) {
        CabecalhoSecao("Transparente", "Você só paga se houver match", "Nada de mensalidade. A taxa só é cobrada quando você decide conhecer a pessoa compatível.")
        Column(Modifier.fillMaxWidth().background(Color.White, Cartao).border(1.dp, Cores.Linha, Cartao).padding(24.dp)) {
            Text("Cadastro", style = MaterialTheme.typography.titleLarge)
            Text("Grátis", fontFamily = Cormorant, fontWeight = FontWeight.Bold, fontSize = 46.sp, color = Cores.Vinho)
            ListaCoracoes(listOf("Perfil completo: como você é", "Preferências: quem você procura", "Cruzamento de dados automático", "Aviso quando houver compatibilidade"), Cores.Vermelho, Cores.Tinta)
            Spacer(Modifier.height(16.dp))
            BotaoContorno("Cadastrar agora", aoCadastrar, Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(26.dp))
        Box {
            Column(
                Modifier.fillMaxWidth().shadow(14.dp, Cartao).background(Brush.linearGradient(listOf(Cores.Vinho, Color(0xFF3A0914))), Cartao).padding(24.dp),
            ) {
                Spacer(Modifier.height(6.dp))
                Text("Conhecer meu par", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("R$ 70", fontFamily = Cormorant, fontWeight = FontWeight.Bold, fontSize = 46.sp, color = Color.White)
                    Text("  por match", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.75f), modifier = Modifier.padding(bottom = 10.dp))
                }
                ListaCoracoes(
                    listOf("Pagamento único, sem mensalidade", "Após ambos pagarem, liberamos o contato", "Ou organizamos um encontro às cegas", "Seus dados só são revelados com o pagamento dos dois"),
                    Cores.Dourado, Color.White,
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("◆ Pix", "▭ Cartão de crédito").forEach {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = Color.White, modifier = Modifier.background(Color.White.copy(alpha = 0.12f), Pilula).padding(horizontal = 12.dp, vertical = 5.dp))
                    }
                }
            }
            Text(
                "Somente quando há match",
                modifier = Modifier.padding(start = 22.dp).graphicsLayer { translationY = -26f }.background(Cores.Dourado, Pilula).padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Cores.Vinho,
            )
        }
    }
}

@Composable
private fun ListaCoracoes(itens: List<String>, corCoracao: Color, corTexto: Color) {
    Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        itens.forEach {
            Row {
                Text("♥", color = corCoracao, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(10.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = corTexto)
            }
        }
    }
}

@Composable
private fun Duvidas() {
    val perguntas = listOf(
        "O cadastro é realmente gratuito?" to "Sim. Cadastrar-se, preencher o perfil e ser avisado de uma compatibilidade não custa nada.",
        "Como funciona o cruzamento de dados?" to "Comparamos como você é com o que outras pessoas procuram, e o que você procura com como elas são. Só avisamos quando a compatibilidade vai nos dois sentidos.",
        "Quando pago os R$ 70?" to "Somente depois que avisamos que existe alguém compatível e você decide que quer conhecer essa pessoa. O pagamento pode ser via Pix ou cartão de crédito.",
        "E se a outra pessoa não pagar?" to "O contato só é liberado quando os dois pagam. Enquanto a outra pessoa não confirmar, nenhum dado é compartilhado.",
        "O que é o encontro às cegas?" to "Em vez de trocar contatos, nós combinamos um local e horário para vocês se conhecerem pessoalmente, em um lugar público e seguro.",
        "Meus dados ficam visíveis para outras pessoas?" to "Não. Seu perfil não é público. Apenas nossa equipe vê seus dados para fazer o cruzamento de dados.",
    )
    Column(Modifier.padding(horizontal = 20.dp).padding(bottom = 52.dp)) {
        CabecalhoSecao("Dúvidas", "Perguntas frequentes")
        perguntas.forEach { (p, r) -> ItemDuvida(p, r) }
    }
}

@Composable
private fun ItemDuvida(pergunta: String, resposta: String) {
    var aberto by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier.fillMaxWidth().padding(bottom = 12.dp).clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, Cores.Linha, RoundedCornerShape(18.dp))
            .clickable(role = Role.Button) { aberto = !aberto }.padding(horizontal = 20.dp, vertical = 16.dp).animateContentSize(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(pergunta, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), modifier = Modifier.weight(1f))
            Text(if (aberto) "–" else "+", color = Cores.Vermelho, fontSize = 24.sp)
        }
        if (aberto) {
            Spacer(Modifier.height(8.dp))
            Text(resposta, style = MaterialTheme.typography.bodyMedium, color = Cores.Suave)
        }
    }
}

@Composable
private fun ChamadaFinal(aoCadastrar: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(Brush.radialGradient(listOf(Cores.Dourado.copy(alpha = 0.2f), Color.Transparent), radius = 700f))
            .padding(horizontal = 24.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            buildAnnotatedString {
                append("A sua rosa pode estar a ")
                withStyle(SpanStyle(color = Cores.Vermelho, fontStyle = FontStyle.Italic)) { append("um cadastro") }
                append(" de distância.")
            },
            style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        BotaoPrincipal("Cadastre-se grátis ♥", aoCadastrar)
    }
}

@Composable
private fun Rodape(aoExcluir: () -> Unit) {
    val ctx = LocalContext.current
    val corTexto = Color(0xFFD9BFC3)
    Column(
        Modifier.fillMaxWidth().background(Color(0xFF22070D)).padding(horizontal = 22.dp).padding(top = 40.dp, bottom = 24.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Logo(44)
            Spacer(Modifier.width(10.dp))
            Text("Solidão Nunca Mais", fontFamily = Cormorant, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
        }
        Spacer(Modifier.height(12.dp))
        Text("Unindo pessoas que combinam de verdade. Para maiores de 18 anos.", style = MaterialTheme.typography.bodyMedium, color = corTexto)
        Spacer(Modifier.height(20.dp))
        val links = listOf(
            "Termos de Uso" to { abrirLink(ctx, Contato.TERMOS) },
            "Política de Privacidade" to { abrirLink(ctx, Contato.PRIVACIDADE) },
            Contato.EMAIL to { enviarEmail(ctx) },
            "WhatsApp: ${Contato.WHATSAPP_TEXTO}" to { abrirWhatsApp(ctx) },
            "Excluir meus dados" to aoExcluir,
        )
        links.forEach { (texto, acao) ->
            Text(
                texto, style = MaterialTheme.typography.bodyMedium, color = corTexto,
                modifier = Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = acao).padding(vertical = 9.dp),
            )
        }
        HorizontalDivider(Modifier.padding(vertical = 18.dp), color = Color.White.copy(alpha = 0.1f))
        Text(
            "© ${Calendar.getInstance().get(Calendar.YEAR)} Solidão Nunca Mais. Todos os direitos reservados.\nFeito com ♥ no Rio Grande do Sul",
            style = MaterialTheme.typography.bodySmall, color = corTexto.copy(alpha = 0.7f),
        )
    }
}

/** Pedido de exclusão dos dados (exigência da Google Play e direito previsto na LGPD). */
@Composable
private fun DialogoExclusao(aoFechar: () -> Unit) {
    val ctx = LocalContext.current
    val mensagem = "Olá! Quero excluir meu cadastro e meus dados da Solidão Nunca Mais.\nNome:\nE-mail ou WhatsApp usado no cadastro:"
    AlertDialog(
        onDismissRequest = aoFechar,
        containerColor = Cores.Creme,
        title = { Text("Excluir meus dados", style = MaterialTheme.typography.headlineMedium) },
        text = {
            Text(
                "Peça a exclusão do seu cadastro pelo WhatsApp ou por e-mail, informando o e-mail ou WhatsApp que você usou. Apagamos seus dados em até 15 dias. Os registros de pagamento ficam guardados por até 5 anos, por obrigação legal.",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = { abrirWhatsApp(ctx, mensagem); aoFechar() }) { Text("Pedir pelo WhatsApp", color = Cores.Vermelho) }
        },
        dismissButton = {
            TextButton(onClick = { enviarEmail(ctx, "Excluir meus dados", mensagem); aoFechar() }) { Text("Pedir por e-mail", color = Cores.Vinho) }
        },
    )
}
