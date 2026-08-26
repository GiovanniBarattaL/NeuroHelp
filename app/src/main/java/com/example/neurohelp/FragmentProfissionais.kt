package com.example.neurohelp

import android.app.Dialog
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentProfissionais : Fragment() {

    private var dialogFiltros: Dialog? = null

    private lateinit var telaProfissionais: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragmentprofissionais,
            container,
            false
        )

        telaProfissionais = view

        val btnFiltros = view.findViewById<View>(R.id.btnFiltros)

        btnFiltros.setOnClickListener {
            abrirFiltros()
        }

        return view
    }

    private fun abrirFiltros() {

        // Evita abrir dois filtros ao mesmo tempo
        if (dialogFiltros?.isShowing == true) {
            return
        }

        val dialog = Dialog(requireContext())

        dialogFiltros = dialog

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.filtros)

        val btnAplicar =
            dialog.findViewById<TextView>(R.id.btnAplicarFiltros)

        val btnLimpar =
            dialog.findViewById<TextView>(R.id.btnLimparFiltros)

        // =========================
        // BOTÃO APLICAR
        // =========================

        btnAplicar.setOnClickListener {

            // Aqui futuramente vamos aplicar
            // os filtros nos profissionais.

            dialog.dismiss()
        }

        // =========================
        // BOTÃO LIMPAR
        // =========================

        btnLimpar.setOnClickListener {

            // Aqui futuramente vamos limpar
            // os filtros selecionados.

            limparFiltros(dialog)

        }

        // =========================
        // MOSTRA O DIALOG
        // =========================

        dialog.show()

        val window = dialog.window

        if (window != null) {

            // Fundo transparente
            window.setBackgroundDrawableResource(
                android.R.color.transparent
            )

            // IMPORTANTE:
            // remove o fundo escuro padrão do Dialog
            window.clearFlags(
                android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
            )

            val larguraPainel = dpToPx(200)

            window.setLayout(
                larguraPainel,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // =========================
            // POSIÇÃO
            // =========================

            val larguraTela =
                resources.displayMetrics.widthPixels

            val margemDireita =
                dpToPx(20)

            val posicaoX =
                larguraTela - larguraPainel - margemDireita

            window.setGravity(
                Gravity.TOP or Gravity.START
            )

            val params = window.attributes

            params.x = posicaoX

            // Distância do topo
            params.y = dpToPx(112)

            window.attributes = params
        }

        // =========================
        // BLUR DA TELA DE FUNDO
        // =========================

        aplicarBlur()

        // Quando fechar o Dialog,
        // remove o blur.
        dialog.setOnDismissListener {

            removerBlur()

            dialogFiltros = null
        }
    }

    // =====================================================
    // APLICA O DESFOQUE NA TELA DE PROFISSIONAIS
    // =====================================================

    private fun aplicarBlur() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            telaProfissionais.setRenderEffect(
                RenderEffect.createBlurEffect(
                    7f,
                    7f,
                    Shader.TileMode.CLAMP
                )
            )
        }
    }

    // =====================================================
    // REMOVE O DESFOQUE
    // =====================================================

    private fun removerBlur() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            telaProfissionais.setRenderEffect(null)
        }
    }

    // =====================================================
    // LIMPAR FILTROS
    // =====================================================

    private fun limparFiltros(dialog: Dialog) {

        val checkPsicologia =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.checkEspecialidade1
            )

        val checkFono =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.checkEspecialidade2
            )

        val checkTerapia =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.checkEspecialidade3
            )

        val checkEspecialidade4 =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.checkEspecialidade4
            )

        val checkEspecialidade5 =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.checkEspecialidade5
            )

        val checkOnline =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.checkOnline
            )

        val checkPresencial =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.checkPresencial
            )

        val checkHibrido =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.checkHibrido
            )

        val check5 =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.check5Estrelas
            )

        val check4 =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.check4Estrelas
            )

        val check3 =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.check3Estrelas
            )

        val check2 =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.check2Estrelas
            )

        val check1 =
            dialog.findViewById<android.widget.CheckBox>(
                R.id.check1Estrela
            )

        checkPsicologia?.isChecked = false
        checkFono?.isChecked = false
        checkTerapia?.isChecked = false
        checkEspecialidade4?.isChecked = false
        checkEspecialidade5?.isChecked = false

        checkOnline?.isChecked = false
        checkPresencial?.isChecked = false
        checkHibrido?.isChecked = false

        check5?.isChecked = false
        check4?.isChecked = false
        check3?.isChecked = false
        check2?.isChecked = false
        check1?.isChecked = false
    }

    // =====================================================
    // DP PARA PX
    // =====================================================

    private fun dpToPx(dp: Int): Int {

        return (
                dp * resources.displayMetrics.density
                ).toInt()
    }

    // =====================================================
    // SEGURANÇA AO SAIR DA TELA
    // =====================================================

    override fun onDestroyView() {

        removerBlur()

        dialogFiltros?.dismiss()

        dialogFiltros = null

        super.onDestroyView()
    }
}