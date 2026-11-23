package br.com.solidaonuncamais.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.solidaonuncamais.app.data.CadastroViewModel
import br.com.solidaonuncamais.app.ui.CadastroTela
import br.com.solidaonuncamais.app.ui.InicioTela
import br.com.solidaonuncamais.app.ui.SucessoTela
import br.com.solidaonuncamais.app.ui.TemaSolidao

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent { TemaSolidao { App() } }
    }
}

private enum class Tela { INICIO, CADASTRO, SUCESSO }

@Composable
private fun App() {
    val vm: CadastroViewModel = viewModel()
    var tela by rememberSaveable { mutableStateOf(Tela.INICIO) }
    var nome by rememberSaveable { mutableStateOf("") }

    BackHandler(enabled = tela != Tela.INICIO) {
        when {
            tela == Tela.CADASTRO && vm.etapa > 0 -> vm.etapa--
            tela == Tela.SUCESSO -> { vm.reiniciar(); tela = Tela.INICIO }
            else -> tela = Tela.INICIO
        }
    }

    AnimatedContent(tela, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "tela") { atual ->
        when (atual) {
            Tela.INICIO -> InicioTela(aoCadastrar = { tela = Tela.CADASTRO })
            Tela.CADASTRO -> CadastroTela(
                vm = vm,
                aoVoltar = { tela = Tela.INICIO },
                aoConcluir = { primeiro -> nome = primeiro; tela = Tela.SUCESSO },
            )
            Tela.SUCESSO -> SucessoTela(nome, aoFechar = { vm.reiniciar(); tela = Tela.INICIO })
        }
    }
}
