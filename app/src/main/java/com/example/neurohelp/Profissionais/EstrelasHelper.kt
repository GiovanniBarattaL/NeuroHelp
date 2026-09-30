package com.example.neurohelp.Profissionais

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.neurohelp.R
import kotlin.math.roundToInt

/** Desenha 5 estrelas (cheia / meia / vazia) dentro de um LinearLayout horizontal. */
object EstrelasHelper {

    fun preencher(
        container: LinearLayout,
        nota: Double,
        tamanhoDp: Int,
        espacoDp: Int = 1
    ) {
        container.removeAllViews()

        val density = container.resources.displayMetrics.density
        val tamanhoPx = (tamanhoDp * density).roundToInt()
        val espacoPx = (espacoDp * density).roundToInt()

        // arredonda para o múltiplo de 0,5 mais próximo (3,7 -> 3,5)
        val notaArredondada = (nota.coerceIn(0.0, 5.0) * 2).roundToInt() / 2.0

        for (posicao in 1..5) {
            val recurso = when {
                notaArredondada >= posicao -> R.drawable.ic_estrela_cheia
                notaArredondada >= posicao - 0.5 -> R.drawable.ic_estrela_meia
                else -> R.drawable.ic_estrela_vazia
            }

            val estrela = ImageView(container.context).apply {
                setImageResource(recurso)
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                layoutParams = LinearLayout.LayoutParams(tamanhoPx, tamanhoPx).apply {
                    if (posicao < 5) marginEnd = espacoPx
                }
            }
            container.addView(estrela)
        }

        container.contentDescription = "Nota $nota de 5"
    }
}
