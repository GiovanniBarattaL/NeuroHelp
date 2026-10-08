package com.example.neurohelp.agenda

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import com.example.neurohelp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Bottom sheet de detalhes da consulta para o PROFISSIONAL (Figma "Group 234"):
 * mostra os dados do paciente e os botões "Editar Consulta" e "Cancelar consulta".
 *
 * O resultado volta pelo [REQUEST_KEY] (FragmentResult) com a consulta no bundle e
 * [EXTRA_ACAO] = [ACAO_EDITAR] ou [ACAO_CANCELAR].
 */
class DetalhesConsultaProfissionalDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val REQUEST_KEY = "detalhes_consulta_profissional"
        const val EXTRA_ACAO = "acao"
        const val ACAO_EDITAR = "editar"
        const val ACAO_CANCELAR = "cancelar"
        const val TAG = "DetalhesConsultaProfissional"

        fun newInstance(consulta: Consulta) = DetalhesConsultaProfissionalDialogFragment().apply {
            arguments = consulta.toBundle()
        }
    }

    // Mesmo tema do outro bottom sheet: fundo transparente, o layout desenha o fundo arredondado
    override fun getTheme(): Int = R.style.TemaBottomSheetConsulta

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.agenda_dialog_consulta_profissional, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val consulta = requireArguments().toConsulta()
        view.preencherDetalhesConsultaProfissional(consulta)

        view.findViewById<View>(R.id.btnEditarConsulta).setOnClickListener {
            devolverAcao(consulta, ACAO_EDITAR)
        }
        view.findViewById<View>(R.id.btnCancelarConsulta).setOnClickListener {
            devolverAcao(consulta, ACAO_CANCELAR)
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

/** Preenche o bottom sheet de detalhes do profissional (agenda_dialog_consulta_profissional). */
fun View.preencherDetalhesConsultaProfissional(consulta: Consulta) {
    val nomeEspecialidade = context.getString(consulta.especialidade.nomeRes)

    fun texto(id: Int) = findViewById<TextView>(id)

    texto(R.id.txtTitulo).text =
        context.getString(R.string.agenda_titulo_consulta, nomeEspecialidade)
    texto(R.id.txtNomePaciente).text =
        context.getString(R.string.agenda_paciente, consulta.paciente ?: "—")

    texto(R.id.txtCpfPaciente).apply {
        text = "CPF ${consulta.pacienteCpf.orEmpty()}"
        visibility = if (consulta.pacienteCpf.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    texto(R.id.txtData).text = "${consulta.inicio.dataPorExtenso()}\n(${consulta.inicio.diaDaSemana()})"
    texto(R.id.txtHorario).text = consulta.inicio.horaFormatada()
    texto(R.id.txtFormato).text = context.getString(consulta.modalidade.nomeRes)
    texto(R.id.txtStatus).text = "Confirmada"

    texto(R.id.txtSobrePaciente).text = consulta.sobrePaciente.orEmpty()

    texto(R.id.txtObservacoes).apply {
        text = consulta.observacoes.orEmpty()
        visibility = if (consulta.observacoes.isNullOrBlank()) View.GONE else View.VISIBLE
    }
    texto(R.id.txtFoco).apply {
        text = "Foco: ${consulta.foco.orEmpty()}"
        visibility = if (consulta.foco.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    // Círculo na cor da especialidade
    findViewById<View>(R.id.framePaciente).backgroundTintList =
        ColorStateList.valueOf(ContextCompat.getColor(context, consulta.especialidade.corRes))
}
