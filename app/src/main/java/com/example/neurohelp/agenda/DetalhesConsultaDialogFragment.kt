package com.example.neurohelp.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.example.neurohelp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Bottom sheet com os detalhes da consulta (Figma).
 * Aberto ao tocar numa notificação de consulta e também pela Agenda.
 *
 * O resultado volta pelo [REQUEST_KEY] (FragmentResult) com a consulta no bundle e
 * [EXTRA_ACAO] = [ACAO_CONFIRMAR] ou [ACAO_ALTERAR_CANCELAR].
 */
class DetalhesConsultaDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val REQUEST_KEY = "detalhes_consulta"
        const val EXTRA_ACAO = "acao"
        const val ACAO_CONFIRMAR = "confirmar"
        const val ACAO_ALTERAR_CANCELAR = "alterar_cancelar"
        const val TAG = "DetalhesConsulta"

        fun newInstance(consulta: Consulta) = DetalhesConsultaDialogFragment().apply {
            arguments = consulta.toBundle()
        }
    }

    // Tema com fundo transparente: o layout desenha o próprio fundo arredondado
    override fun getTheme(): Int = R.style.TemaBottomSheetConsulta

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.agenda_dialog_consulta_detalhes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val consulta = requireArguments().toConsulta()
        view.preencherDetalhesConsulta(consulta)

        view.findViewById<View>(R.id.btnConfirmar).setOnClickListener {
            devolverAcao(consulta, ACAO_CONFIRMAR)
        }
        view.findViewById<View>(R.id.btnAlterarCancelar).setOnClickListener {
            devolverAcao(consulta, ACAO_ALTERAR_CANCELAR)
        }
    }

    override fun onStart() {
        super.onStart()
        // Abre já totalmente expandido (sem parar na metade da altura)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
    }

    private fun devolverAcao(consulta: Consulta, acao: String) {
        val resultado = consulta.toBundle().apply { putString(EXTRA_ACAO, acao) }
        parentFragmentManager.setFragmentResult(REQUEST_KEY, resultado)
        dismiss()
    }
}
