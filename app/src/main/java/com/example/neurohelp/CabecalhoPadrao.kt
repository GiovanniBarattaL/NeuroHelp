package com.example.neurohelp

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.neurohelp.Perfil.FragmentPerfil
import com.example.neurohelp.notificacoes.abrirNotificacoes

/**
 * Liga os botões do cabeçalho padrão (layout header_padrao): voltar, sino e avatar.
 *
 * @param mostrarVoltar  false nas telas raiz (ex.: Home), onde a seta não faz sentido.
 * @param abrirSino      false na própria tela de Notificações.
 * @param abrirPerfil    false na própria tela de Perfil.
 * @param aoVoltar       ação da seta; por padrão volta uma tela na pilha.
 */
fun Fragment.configurarCabecalhoPadrao(
    view: View,
    mostrarVoltar: Boolean = true,
    abrirSino: Boolean = true,
    abrirPerfil: Boolean = true,
    aoVoltar: () -> Unit = { parentFragmentManager.popBackStack() }
) {
    val voltar = view.findViewById<View>(R.id.imgVoltar)
    if (mostrarVoltar) {
        voltar.setOnClickListener { aoVoltar() }
    } else {
        voltar.visibility = View.GONE
        // Sem a seta, a logo ganha o respiro do lado esquerdo
        val logo = view.findViewById<View>(R.id.imgLogo)
        (logo.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
            it.marginStart = (16 * resources.displayMetrics.density).toInt()
            logo.layoutParams = it
        }
    }

    if (abrirSino) {
        view.findViewById<View>(R.id.imgNotificacao).setOnClickListener {
            abrirNotificacoes()
        }
    }

    if (abrirPerfil) {
        view.findViewById<View>(R.id.imgPerfil).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentPerfil())
                .addToBackStack(null)
                .commit()
        }
    }
}
