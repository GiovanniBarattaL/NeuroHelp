package com.example.neurohelp

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView

/** Ajusta o conteúdo existente; não muda o tamanho do texto escolhido pelo usuário. */
fun View.ajustarConteudoResponsivo() {
    val config = resources.configuration
    val compacto = config.screenWidthDp < 380 || config.fontScale > 1.15f
    val espaco = (8 * resources.displayMetrics.density).toInt()

    fun empilhar(linha: LinearLayout) {
        linha.orientation = LinearLayout.VERTICAL
        for (i in 0 until linha.childCount) {
            val filho = linha.getChildAt(i)
            if (filho.javaClass == View::class.java) {
                filho.visibility = View.GONE // divisores da antiga linha de resumo
            } else if (filho is android.widget.ImageView) {
                (filho.layoutParams as? LinearLayout.LayoutParams)?.apply {
                    gravity = android.view.Gravity.CENTER_HORIZONTAL
                    filho.layoutParams = this
                }
            } else {
                filho.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { if (i > 0) topMargin = espaco }
            }
        }
    }

    fun visitar(v: View) {
        // A Home profissional já limita o bloco de conteúdo sem limitar seu banner.
        if (v is ScrollView || v is NestedScrollView) {
            val conteudo = (v as ViewGroup).getChildAt(0)
            if (conteudo != null && conteudo.id != R.id.conteudoHomeProfissional &&
                conteudo.id != R.id.conteudoPrivacidadeProf) {
                conteudo.limitarLargura((720 * resources.displayMetrics.density).toInt())
            }
        }
        if (v is LinearLayout && v.orientation == LinearLayout.HORIZONTAL && compacto) {
            val filhos = (0 until v.childCount).map { v.getChildAt(it) }
            val acoes = filhos.count {
                it is android.widget.Button ||
                    (it.id != View.NO_ID && resources.getResourceEntryName(it.id).startsWith("btn") &&
                        it is android.widget.TextView)
            }
            if (v.id == R.id.infoConsulta || v.id == R.id.topoPerfilProfissional ||
                v.id == R.id.listaConteudosResponsavel || acoes >= 2) {
                empilhar(v)
            }
        }
        if (v is ViewGroup) for (i in 0 until v.childCount) visitar(v.getChildAt(i))
    }
    visitar(this)
}
