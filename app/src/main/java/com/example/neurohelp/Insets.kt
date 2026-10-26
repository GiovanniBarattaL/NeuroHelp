package com.example.neurohelp

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Tratamento das barras do sistema (status bar, barra de navegação, recorte/notch e teclado).
 *
 * Com targetSdk 36 o Android 15+ desenha o app por baixo das barras. Antes o app "empurrava" o
 * conteúdo com um marginTop de 30dp fixo, que só servia para uma status bar de ~24dp: em aparelhos
 * com notch, ilha ou status bar maior o cabeçalho ficava escondido. Agora o espaço é medido de
 * verdade, em qualquer versão do Android.
 */

/**
 * Liga o modo tela cheia (edge-to-edge) na Activity. Chamar ANTES do setContentView.
 *
 * O app só tem tema claro (fundo branco), então os ícones das barras são SEMPRE escuros.
 * Sem isso, em celulares no modo escuro o Android deixaria os ícones brancos sobre o
 * cabeçalho branco e a hora/bateria sumiriam.
 */
fun ComponentActivity.ativarTelaCheia() {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    )
}

/**
 * Para telas inteiras (login, cadastro...): aplica as barras do sistema e o teclado como
 * padding da raiz, então nada fica escondido nem atrás do teclado.
 */
fun Activity.aplicarInsetsNaRaiz(raiz: View) {
    raiz.configurarCamposNumericos()
    val padL = raiz.paddingLeft
    val padT = raiz.paddingTop
    val padR = raiz.paddingRight
    val padB = raiz.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(raiz) { v, insets ->
        val barras = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        val teclado = insets.getInsets(WindowInsetsCompat.Type.ime())
        v.setPadding(
            padL + barras.left,
            padT + barras.top,
            padR + barras.right,
            padB + maxOf(barras.bottom, teclado.bottom)
        )
        // Não consome: os filhos (ex.: BottomNavigationView) também querem ler os insets.
        insets
    }
    ViewCompat.requestApplyInsets(raiz)
}

/**
 * A raiz é a única responsável pelas laterais e pela base da tela principal.
 * O menu Material não deve reaplicar o mesmo espaço do sistema/teclado.
 * Ao editar, o menu some e o formulário usa toda a área acima do teclado.
 * O topo continua sendo tratado pelo cabeçalho de cada fragment.
 */
fun Activity.aplicarInsetsLateraisNaRaiz(raiz: View) {
    val padL = raiz.paddingLeft
    val padT = raiz.paddingTop
    val padR = raiz.paddingRight
    val padB = raiz.paddingBottom
    val menu = raiz.findViewById<View>(R.id.bottomNavigation)
    val menuPadL = menu.paddingLeft
    val menuPadT = menu.paddingTop
    val menuPadR = menu.paddingRight
    val menuPadB = menu.paddingBottom

    // Substitui o listener automático do Material, que em versões antigas do
    // Android pode interpretar o inset inferior do teclado como barra do sistema.
    ViewCompat.setOnApplyWindowInsetsListener(menu) { v, insets ->
        v.setPadding(menuPadL, menuPadT, menuPadR, menuPadB)
        insets
    }
    ViewCompat.setOnApplyWindowInsetsListener(raiz) { v, insets ->
        val barras = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        val teclado = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
        val digitando = insets.isVisible(WindowInsetsCompat.Type.ime())
        menu.visibility = if (digitando) View.GONE else View.VISIBLE
        v.setPadding(
            padL + barras.left, padT, padR + barras.right,
            padB + maxOf(barras.bottom, teclado)
        )
        insets
    }
    ViewCompat.requestApplyInsets(raiz)
}

/**
 * Para o cabeçalho padrão: soma a altura da barra de status ao padding de cima e à altura da
 * view, assim o fundo branco do cabeçalho cobre a barra e o conteúdo (logo, sino) fica abaixo dela.
 */
fun View.aplicarInsetNoTopo() {
    if (getTag(R.id.tag_inset_topo) == true) return
    setTag(R.id.tag_inset_topo, true)
    val padTopOriginal = paddingTop
    val alturaOriginal = layoutParams?.height ?: 0
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val topo = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        ).top
        v.setPadding(v.paddingLeft, padTopOriginal + topo, v.paddingRight, v.paddingBottom)
        val lp = v.layoutParams
        if (lp != null && alturaOriginal > 0) {
            lp.height = alturaOriginal + topo
            v.layoutParams = lp
        }
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

/**
 * Em tablets/paisagem, impede que o conteúdo fique esticado na tela toda: centraliza com no máximo
 * [maxPx] de largura (a sobra vira padding lateral). Em celular em retrato a largura já é menor
 * que o máximo, então nada muda.
 */
fun View.limitarLargura(maxPx: Int) {
    if (getTag(R.id.tag_limite_largura) == true) return
    setTag(R.id.tag_limite_largura, true)
    val padEsq = paddingLeft
    val padDir = paddingRight
    addOnLayoutChangeListener { v, left, _, right, _, _, _, _, _ ->
        val lateral = (((right - left) - maxPx) / 2).coerceAtLeast(0)
        if (v.paddingLeft != padEsq + lateral || v.paddingRight != padDir + lateral) {
            // post: não mexer no padding no meio de um passe de layout
            v.post { v.setPadding(padEsq + lateral, v.paddingTop, padDir + lateral, v.paddingBottom) }
        }
    }
}

/** Formulários (login/cadastro): usa [R.dimen.largura_max_formulario]. */
fun Activity.limitarLarguraDoConteudo(conteudo: View) {
    conteudo.limitarLargura(resources.getDimensionPixelSize(R.dimen.largura_max_formulario))
}
