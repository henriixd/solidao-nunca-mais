package br.com.solidaonuncamais.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import br.com.solidaonuncamais.app.data.CadastroViewModel
import br.com.solidaonuncamais.app.ui.CadastroTela
import br.com.solidaonuncamais.app.ui.InicioTela
import br.com.solidaonuncamais.app.ui.LocalAnimacoesContinuas
import br.com.solidaonuncamais.app.ui.SucessoTela
import br.com.solidaonuncamais.app.ui.TemaSolidao
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Gera imagens das telas para conferir o visual sem precisar de um celular. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h852dp-xxhdpi")
class CapturasTest {

    private fun capturar(nome: String, tela: @Composable () -> Unit) =
        captureRoboImage("build/capturas/$nome.png") {
            CompositionLocalProvider(LocalAnimacoesContinuas provides false) { TemaSolidao { tela() } }
        }

    @Test fun inicio() = capturar("01a-inicio") { InicioTela(aoCadastrar = {}) }
    @Test fun inicioComoFunciona() = capturar("01b-inicio-como-funciona") { InicioTela(aoCadastrar = {}, itemInicial = 1) }
    @Test fun inicioSimbolo() = capturar("01c-inicio-simbolo") { InicioTela(aoCadastrar = {}, itemInicial = 2) }
    @Test fun inicioValores() = capturar("01d-inicio-valores") { InicioTela(aoCadastrar = {}, itemInicial = 3) }
    @Test fun inicioDuvidas() = capturar("01e-inicio-duvidas") { InicioTela(aoCadastrar = {}, itemInicial = 4) }
    @Test fun inicioRodape() = capturar("01f-inicio-final") { InicioTela(aoCadastrar = {}, itemInicial = 5) }

    private fun etapa(n: Int, preencher: CadastroViewModel.() -> Unit = {}) {
        val vm = CadastroViewModel().apply { preencher(); etapa = n - 1 }
        capturar("0${n + 1}-cadastro-etapa-$n") { CadastroTela(vm, aoVoltar = {}, aoConcluir = {}) }
    }

    @Test fun etapa1() = etapa(1) { respostas["nome"] = "Ana Paula"; respostas["genero"] = "Mulher"; respostas["uf"] = "RS"; respostas["nascimento"] = "1990-05-12" }
    @Test fun etapa2() = etapa(2) { respostas["nome"] = "Ana Paula"; respostas["corpo"] = "Mediano(a)" }
    @Test fun etapa3() = etapa(3) { respostas["fuma"] = "Não fumo"; respostas["bebe"] = "Socialmente" }
    @Test fun etapa4() = etapa(4) { respostas["personalidade"] = listOf("Romântico(a)", "Calmo(a)") }
    @Test fun etapa5() = etapa(5) { respostas["procura_genero"] = "Homem"; respostas["intencao"] = "Casamento" }
    @Test fun etapa6() = etapa(6) { respostas["termos"] = true }

    @Test fun sucesso() = capturar("08-sucesso") { SucessoTela("Ana", aoFechar = {}) }
}
