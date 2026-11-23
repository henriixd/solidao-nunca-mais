package br.com.solidaonuncamais.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.solidaonuncamais.app.R
import br.com.solidaonuncamais.app.data.CadastroApi
import br.com.solidaonuncamais.app.data.CadastroViewModel
import br.com.solidaonuncamais.app.data.Contato
import br.com.solidaonuncamais.app.data.ETAPAS
import br.com.solidaonuncamais.app.data.INCENTIVOS
import br.com.solidaonuncamais.app.data.Opcoes
import br.com.solidaonuncamais.app.data.Pergunta
import br.com.solidaonuncamais.app.data.TipoAceite
import br.com.solidaonuncamais.app.data.TipoTexto
import br.com.solidaonuncamais.app.data.dataDeMillis
import br.com.solidaonuncamais.app.data.dataParaExibir
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.roundToInt

private val Campo = RoundedCornerShape(14.dp)

/** Cadastro em 6 etapas, igual ao formulário do site. */
@Composable
fun CadastroTela(vm: CadastroViewModel, aoVoltar: () -> Unit, aoConcluir: (String) -> Unit) {
    val rolagens = remember { List(ETAPAS.size) { ScrollState(0) } }
    val escopo = rememberCoroutineScope()
    val erros = remember { mutableStateListOf<String>() }
    val posicoes = remember { mutableStateMapOf<String, Float>() }
    var enviando by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf<String?>(null) }
    val total = ETAPAS.size
    val etapa = vm.etapa

    fun irPara(i: Int) {
        erros.clear()
        vm.etapa = i
        escopo.launch { rolagens[i].scrollTo(0) }
    }

    fun continuar() {
        val (ruins, msg) = vm.validar(etapa)
        erros.clear(); erros.addAll(ruins)
        if (ruins.isNotEmpty()) {
            msg?.let { aviso = it }
            val y = posicoes[ruins.first()] ?: 0f
            escopo.launch { rolagens[etapa].animateScrollTo((y - 40f).coerceAtLeast(0f).roundToInt()) }
            return
        }
        if (etapa < total - 1) { irPara(etapa + 1); return }
        enviando = true
        escopo.launch {
            val erro = CadastroApi.enviar(vm.montarEnvio())
            enviando = false
            if (erro == null) aoConcluir(vm.primeiroNome()) else aviso = erro
        }
    }

    Column(Modifier.fillMaxSize().background(Cores.Creme).imePadding()) {
        // Topo: voltar + trilha com as rosas
        Surface(color = Color.White, shadowElevation = 1.dp) {
            Column(Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 18.dp).padding(top = 6.dp, bottom = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "←", fontSize = 26.sp, color = Cores.Vinho,
                        modifier = Modifier.clip(CircleShape).clickable(role = Role.Button, onClickLabel = "Voltar ao início", onClick = aoVoltar).padding(10.dp),
                    )
                    Text("Cadastro gratuito", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                }
                Trilha(etapa, total)
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        buildAnnotatedString {
                            append("Etapa ")
                            withStyle(SpanStyle(color = Cores.Vinho, fontWeight = FontWeight.SemiBold)) { append("${etapa + 1}") }
                            append(" de ")
                            withStyle(SpanStyle(color = Cores.Vinho, fontWeight = FontWeight.SemiBold)) { append("$total") }
                        },
                        style = MaterialTheme.typography.bodySmall, color = Cores.Suave, modifier = Modifier.weight(1f),
                    )
                    Pontos(etapa, total)
                }
            }
        }

        // Perguntas da etapa
        AnimatedContent(
            targetState = etapa,
            transitionSpec = {
                val frente = targetState > initialState
                (slideInHorizontally(tween(400)) { if (frente) it / 6 else -it / 6 } + fadeIn(tween(400))) togetherWith fadeOut(tween(150))
            },
            modifier = Modifier.weight(1f),
            label = "etapa",
        ) { indice ->
            val e = ETAPAS[indice]
            Column(Modifier.fillMaxSize().verticalScroll(rolagens[indice]).padding(horizontal = 20.dp, vertical = 22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(e.emoji, fontSize = 32.sp)
                    Spacer(Modifier.width(10.dp))
                    val titulo = if (indice == 1 && vm.primeiroNome().isNotEmpty()) "Prazer, ${vm.primeiroNome()}! Como você é?" else e.titulo
                    Text(titulo, style = MaterialTheme.typography.headlineMedium)
                }
                Spacer(Modifier.height(4.dp))
                Text(e.descricao, style = MaterialTheme.typography.bodyMedium, color = Cores.Suave)
                Spacer(Modifier.height(20.dp))
                e.perguntas.forEach { p ->
                    Box(Modifier.fillMaxWidth().padding(bottom = 22.dp).onGloballyPositioned { posicoes[p.id] = it.positionInParent().y }) {
                        CampoPergunta(p, vm, erro = p.id in erros, aoMudar = { erros.remove(p.id) })
                    }
                }
            }
        }

        // Navegação
        Surface(color = Color.White, shadowElevation = 6.dp) {
            Column(Modifier.windowInsetsPadding(WindowInsets.navigationBars).padding(horizontal = 18.dp, vertical = 12.dp)) {
                if (etapa > 0 && INCENTIVOS.getOrNull(etapa - 1) != null) {
                    Text(
                        INCENTIVOS[etapa - 1], style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = Cores.Vermelho, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), textAlign = TextAlign.Center,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (etapa > 0) BotaoContorno("← Voltar", { irPara(etapa - 1) }, Modifier.weight(1f))
                    BotaoPrincipal(
                        when {
                            enviando -> "Enviando…"
                            etapa == total - 1 -> "Enviar cadastro ♥"
                            else -> "Continuar →"
                        },
                        aoClicar = { if (!enviando) continuar() },
                        habilitado = !enviando,
                        modifier = Modifier.weight(if (etapa > 0) 1.4f else 1f),
                    )
                }
            }
        }
    }

    aviso?.let { texto ->
        AlertDialog(
            onDismissRequest = { aviso = null },
            containerColor = Cores.Creme,
            text = { Text(texto, style = MaterialTheme.typography.bodyLarge) },
            confirmButton = { TextButton(onClick = { aviso = null }) { Text("Entendi", color = Cores.Vermelho) } },
        )
    }
}

/** Linha de progresso com a rosa vermelha caminhando até a amarela. */
@Composable
private fun Trilha(etapa: Int, total: Int) {
    val fracao by animateFloatAsState(etapa / (total - 1f), tween(800), label = "progresso")
    BoxWithConstraints(Modifier.fillMaxWidth().height(58.dp)) {
        val larguraRosa = 34.dp
        Box(Modifier.align(Alignment.BottomCenter).padding(horizontal = 16.dp).fillMaxWidth().height(6.dp).padding(bottom = 0.dp)
            .clip(CircleShape).background(Cores.Rosado)) {
            Box(Modifier.fillMaxWidth(fracao.coerceAtLeast(0.001f)).height(6.dp).background(Cores.Progresso))
        }
        Image(
            painterResource(R.drawable.rosa_amarela), contentDescription = null,
            modifier = Modifier.align(Alignment.BottomEnd).height(52.dp).padding(bottom = 3.dp).graphicsLayer { rotationZ = -8f; transformOrigin = TransformOrigin(0.5f, 1f) },
        )
        Image(
            painterResource(R.drawable.rosa_vermelha), contentDescription = null,
            modifier = Modifier.align(Alignment.BottomStart).height(54.dp).padding(bottom = 3.dp)
                .offset(x = (maxWidth - larguraRosa - 44.dp) * fracao)
                .graphicsLayer { rotationZ = 8f; transformOrigin = TransformOrigin(0.5f, 1f) },
        )
    }
}

@Composable
private fun Pontos(etapa: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(total) { i ->
            val largura by animateFloatAsState(if (i == etapa) 22f else 8f, spring(Spring.DampingRatioMediumBouncy), label = "ponto")
            Box(
                Modifier.size(width = largura.dp, height = 8.dp).clip(CircleShape).background(
                    when {
                        i == etapa -> Cores.Vermelho
                        i < etapa -> Cores.Dourado
                        else -> Cores.Linha
                    }
                )
            )
        }
    }
}

@Composable
private fun Rotulo(texto: String, erro: Boolean, dica: String? = null, valor: String? = null) {
    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.Bottom) {
        Text(texto, style = MaterialTheme.typography.labelMedium, color = if (erro) Cores.Erro else Cores.Tinta, modifier = Modifier.weight(1f))
        if (dica != null) Text(dica, style = MaterialTheme.typography.bodySmall, color = Cores.Suave)
        if (valor != null) Text(valor, fontFamily = Cormorant, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Cores.Vinho)
    }
}

@Composable
private fun CampoPergunta(p: Pergunta, vm: CadastroViewModel, erro: Boolean, aoMudar: () -> Unit) {
    when (p) {
        is Pergunta.Texto -> {
            Column {
                Rotulo(p.rotulo, erro)
                OutlinedTextField(
                    value = vm.texto(p.id),
                    onValueChange = { vm.respostas[p.id] = it.take(200); aoMudar() },
                    placeholder = { Text(p.exemplo, color = Cores.Suave.copy(alpha = 0.7f)) },
                    singleLine = true,
                    isError = erro,
                    shape = Campo,
                    colors = coresCampo(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (p.tipo) {
                            TipoTexto.EMAIL -> KeyboardType.Email
                            TipoTexto.TELEFONE -> KeyboardType.Phone
                            else -> KeyboardType.Text
                        },
                        capitalization = if (p.tipo == TipoTexto.NOME || p.tipo == TipoTexto.CIDADE) KeyboardCapitalization.Words else KeyboardCapitalization.None,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        is Pergunta.Nascimento -> CampoNascimento(p, vm, erro, aoMudar)
        is Pergunta.Estado -> CampoEstado(p, vm, erro, aoMudar)
        is Pergunta.Unica -> {
            Column {
                Rotulo(p.rotulo, erro)
                GrupoOpcoes(p.opcoes, selecionadas = listOf(vm.texto(p.id)), cartoes = p.cartoes) { v ->
                    vm.respostas[p.id] = v; aoMudar()
                }
            }
        }
        is Pergunta.Multipla -> {
            var tremer by remember { mutableStateOf(0) }
            val deslocamento by animateFloatAsState(if (tremer % 2 == 1) 1f else 0f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessHigh), label = "tremer")
            Column(Modifier.graphicsLayer { translationX = deslocamento * 14f }) {
                Rotulo(p.rotulo, erro || tremer % 2 == 1, dica = p.dica)
                val atuais = vm.lista(p.id)
                GrupoOpcoes(p.opcoes, selecionadas = atuais, cartoes = false, tantoFaz = p.tantoFaz, tantoFazMarcado = atuais.isEmpty()) { v ->
                    when {
                        v == "" -> vm.respostas[p.id] = emptyList<String>()
                        v in atuais -> vm.respostas[p.id] = atuais - v
                        p.max != null && atuais.size >= p.max -> { tremer++; return@GrupoOpcoes }
                        else -> vm.respostas[p.id] = atuais + v
                    }
                    if (tremer % 2 == 1) tremer++
                    aoMudar()
                }
            }
        }
        is Pergunta.Deslizante -> {
            val valor = vm.numero(p.id)
            Column {
                Rotulo(p.rotulo, false, valor = "$valor${p.unidade}")
                Slider(
                    value = valor.toFloat(),
                    onValueChange = { vm.respostas[p.id] = it.roundToInt() },
                    valueRange = p.min.toFloat()..p.max.toFloat(),
                    colors = coresSlider(),
                )
            }
        }
        is Pergunta.Faixa -> {
            val (a, b) = vm.faixa(p.id)
            val texto = if (p.qualquer && a == p.min && b == p.max) "Tanto faz"
            else "$a a $b${if (b == p.max && p.id == "idade") "+" else ""}${p.unidade}"
            Column {
                Rotulo(p.rotulo, false, valor = texto)
                RangeSlider(
                    value = a.toFloat()..b.toFloat(),
                    onValueChange = { r -> vm.respostas[p.id] = listOf(r.start.roundToInt(), r.endInclusive.roundToInt()) },
                    valueRange = p.min.toFloat()..p.max.toFloat(),
                    colors = coresSlider(),
                )
            }
        }
        is Pergunta.TextoLongo -> {
            Column {
                Rotulo(p.rotulo, false, dica = "opcional")
                OutlinedTextField(
                    value = vm.texto(p.id),
                    onValueChange = { vm.respostas[p.id] = it.take(2000) },
                    placeholder = { Text(p.exemplo, color = Cores.Suave.copy(alpha = 0.7f)) },
                    shape = Campo,
                    colors = coresCampo(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 110.dp),
                )
            }
        }
        is Pergunta.Aceite -> CampoAceite(p, vm, erro, aoMudar)
    }
}

/** Botões de opção em formato de pílula (ou cartões com ícone), como no site. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GrupoOpcoes(
    opcoes: List<String>,
    selecionadas: List<String>,
    cartoes: Boolean,
    tantoFaz: Boolean = false,
    tantoFazMarcado: Boolean = false,
    aoEscolher: (String) -> Unit,
) {
    if (cartoes) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            opcoes.chunked(2).forEach { linha ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    linha.forEach { v -> CartaoOpcao(v, v in selecionadas, Modifier.weight(1f)) { aoEscolher(v) } }
                    if (linha.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    } else {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (tantoFaz) BotaoPilula("💞 Tanto faz", tantoFazMarcado, dourada = true) { aoEscolher("") }
            opcoes.forEach { v -> BotaoPilula(v, v in selecionadas) { aoEscolher(v) } }
        }
    }
}

@Composable
private fun BotaoPilula(texto: String, marcada: Boolean, dourada: Boolean = false, aoClicar: () -> Unit) {
    val escala by animateFloatAsState(if (marcada) 1f else 0.97f, spring(Spring.DampingRatioMediumBouncy), label = "pilula")
    val fundo = when { marcada && dourada -> Cores.Dourado; marcada -> Cores.Vermelho; else -> Cores.Creme }
    val borda = if (marcada) fundo else Cores.Linha
    Text(
        texto,
        modifier = Modifier.graphicsLayer { scaleX = escala; scaleY = escala }
            .clip(Pilula).background(fundo).border(1.5.dp, borda, Pilula)
            .clickable(role = Role.Checkbox, onClick = aoClicar)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = when { marcada && dourada -> Cores.Vinho; marcada -> Color.White; else -> Cores.Tinta },
    )
}

@Composable
private fun CartaoOpcao(texto: String, marcado: Boolean, modifier: Modifier, aoClicar: () -> Unit) {
    val forma = RoundedCornerShape(16.dp)
    val escala by animateFloatAsState(if (marcado) 1.18f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "icone")
    Column(
        modifier.clip(forma).background(if (marcado) Cores.Vermelho else Cores.Creme).border(1.5.dp, if (marcado) Cores.Vermelho else Cores.Linha, forma)
            .clickable(role = Role.RadioButton, onClick = aoClicar).padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Opcoes.icones[texto]?.let { Text(it, fontSize = 26.sp, modifier = Modifier.graphicsLayer { scaleX = escala; scaleY = escala; rotationZ = if (marcado) -8f else 0f }) }
        Spacer(Modifier.height(4.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = if (marcado) Color.White else Cores.Tinta, textAlign = TextAlign.Center, lineHeight = 18.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoNascimento(p: Pergunta.Nascimento, vm: CadastroViewModel, erro: Boolean, aoMudar: () -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    val valor = vm.texto(p.id)
    Column {
        Rotulo(p.rotulo, erro)
        CaixaClicavel(if (valor.isEmpty()) "dd/mm/aaaa" else dataParaExibir(valor), vazio = valor.isEmpty(), erro = erro) { aberto = true }
    }
    if (aberto) {
        val anoAtual = Calendar.getInstance().get(Calendar.YEAR)
        val hoje = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis
        val estado = rememberDatePickerState(
            initialDisplayMode = DisplayMode.Input,
            yearRange = 1920..anoAtual,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= hoje
            },
        )
        DatePickerDialog(
            onDismissRequest = { aberto = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { vm.respostas[p.id] = dataDeMillis(it); aoMudar() }
                    aberto = false
                }) { Text("OK", color = Cores.Vermelho) }
            },
            dismissButton = { TextButton(onClick = { aberto = false }) { Text("Cancelar", color = Cores.Vinho) } },
            colors = DatePickerDefaults.colors(containerColor = Cores.Creme),
        ) {
            DatePicker(
                state = estado,
                title = { Text("Data de nascimento", modifier = Modifier.padding(start = 24.dp, top = 16.dp), style = MaterialTheme.typography.labelLarge) },
                colors = DatePickerDefaults.colors(containerColor = Cores.Creme, selectedDayContainerColor = Cores.Vermelho, todayDateBorderColor = Cores.Vermelho),
            )
        }
    }
}

@Composable
private fun CampoEstado(p: Pergunta.Estado, vm: CadastroViewModel, erro: Boolean, aoMudar: () -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    val valor = vm.texto(p.id)
    Column {
        Rotulo(p.rotulo, erro)
        Box {
            CaixaClicavel(valor.ifEmpty { "Escolha o estado (UF)" }, vazio = valor.isEmpty(), erro = erro, seta = true) { aberto = true }
            DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }, modifier = Modifier.heightIn(max = 320.dp)) {
                Opcoes.uf.forEach { uf ->
                    DropdownMenuItem(text = { Text(uf) }, onClick = { vm.respostas[p.id] = uf; aoMudar(); aberto = false })
                }
            }
        }
    }
}

@Composable
private fun CaixaClicavel(texto: String, vazio: Boolean, erro: Boolean, seta: Boolean = false, aoClicar: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(Campo).background(Cores.Creme).border(1.5.dp, if (erro) Cores.Erro else Cores.Linha, Campo)
            .clickable(role = Role.Button, onClick = aoClicar).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(texto, style = MaterialTheme.typography.bodyLarge, color = if (vazio) Cores.Suave.copy(alpha = 0.7f) else Cores.Tinta, modifier = Modifier.weight(1f))
        Text(if (seta) "▾" else "📅", color = Cores.Vinho)
    }
}

@Composable
private fun CampoAceite(p: Pergunta.Aceite, vm: CadastroViewModel, erro: Boolean, aoMudar: () -> Unit) {
    val ctx = LocalContext.current
    val forte = SpanStyle(fontWeight = FontWeight.SemiBold, color = Cores.Tinta)
    val estiloLink = TextLinkStyles(SpanStyle(color = Cores.Vermelho, fontWeight = FontWeight.Medium))
    val texto = buildAnnotatedString {
        when (p.tipo) {
            TipoAceite.MAIOR18 -> {
                append("Declaro que tenho "); withStyle(forte) { append("18 anos ou mais") }; append(" e que as informações são verdadeiras.")
            }
            TipoAceite.TERMOS -> {
                append("Li e aceito os ")
                withLink(LinkAnnotation.Clickable("termos", estiloLink) { abrirLink(ctx, Contato.TERMOS) }) { append("Termos de Uso") }
                append(" e a ")
                withLink(LinkAnnotation.Clickable("privacidade", estiloLink) { abrirLink(ctx, Contato.PRIVACIDADE) }) { append("Política de Privacidade") }
                append(".")
            }
            TipoAceite.SENSIVEIS -> {
                withStyle(forte) { append("Autorizo, de forma específica,") }
                append(" o uso das informações sobre ")
                withStyle(forte) { append("cor da pele, religião, posição política e o gênero que procuro") }
                append(" (dados sensíveis pela LGPD) ")
                withStyle(forte) { append("apenas") }
                append(" para encontrar pessoas compatíveis comigo. Posso retirar esta autorização quando quiser.")
            }
        }
    }
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(if (erro) Cores.Erro.copy(alpha = 0.06f) else Color.Transparent)
            .clickable(role = Role.Checkbox) { vm.respostas[p.id] = !vm.marcado(p.id); aoMudar() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            checked = vm.marcado(p.id),
            onCheckedChange = { vm.respostas[p.id] = it; aoMudar() },
            colors = CheckboxDefaults.colors(checkedColor = Cores.Vermelho, uncheckedColor = if (erro) Cores.Erro else Cores.Suave),
        )
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = if (erro) Cores.Erro else Cores.Suave, modifier = Modifier.padding(top = 12.dp, end = 8.dp))
    }
}

@Composable
private fun coresCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Cores.Vermelho,
    unfocusedBorderColor = Cores.Linha,
    errorBorderColor = Cores.Erro,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Cores.Creme,
    errorContainerColor = Cores.Creme,
    cursorColor = Cores.Vermelho,
)

@Composable
private fun coresSlider() = SliderDefaults.colors(
    thumbColor = Cores.Vermelho,
    activeTrackColor = Cores.Vermelho,
    inactiveTrackColor = Cores.Rosado,
    activeTickColor = Color.Transparent,
    inactiveTickColor = Color.Transparent,
)

/** Tela de sucesso, com as duas rosas se encontrando. */
@Composable
fun SucessoTela(nome: String, aoFechar: () -> Unit) {
    var visto by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visto = true }
    val p by animateFloatAsState(if (visto) 1f else 0f, tween(1200), label = "encontro")
    Column(
        Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Cores.Creme, Cores.Rosado)))
            .windowInsetsPadding(WindowInsets.statusBars).windowInsetsPadding(WindowInsets.navigationBars).padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.size(width = 230.dp, height = 180.dp)) {
            Image(
                painterResource(R.drawable.rosa_amarela), null,
                modifier = Modifier.align(Alignment.BottomEnd).height(150.dp)
                    .graphicsLayer { translationX = (1 - p) * 300f; rotationZ = 9f + (1 - p) * 21f; alpha = p; transformOrigin = TransformOrigin(0.4f, 1f) },
            )
            Image(
                painterResource(R.drawable.rosa_vermelha), null,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 30.dp).height(160.dp)
                    .graphicsLayer { translationX = -(1 - p) * 300f; rotationZ = -10f - (1 - p) * 20f; alpha = p; transformOrigin = TransformOrigin(0.75f, 1f) },
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(if (nome.isNotEmpty()) "Cadastro recebido, $nome!" else "Cadastro recebido!", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        Text(
            "Agora é com a gente. Assim que o cruzamento de dados encontrar alguém compatível com você, avisaremos pelo WhatsApp e pelo e-mail. Fique de olho ♥",
            style = MaterialTheme.typography.bodyLarge, color = Cores.Suave, textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        BotaoPrincipal("Voltar ao início", aoFechar)
    }
}
