package com.example.neurohelp.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.neurohelp.R
import com.example.neurohelp.configurarCabecalhoPadrao

/**
 * Tela "Reagendar ou confirmar consulta" (Figma).
 * Aberta pelo botão "Alterar ou cancelar" do bottom sheet de detalhes.
 */
class FragmentConsultaDetalhe : Fragment() {

    companion object {
        fun newInstance(consulta: Consulta) = FragmentConsultaDetalhe().apply {
            arguments = consulta.toBundle()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.agenda_reagendar_consulta, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val consulta = requireArguments().toConsulta()
        view.preencherDetalhesConsulta(consulta)

        configurarCabecalhoPadrao(view)

        val etConsideracoes = view.findViewById<EditText>(R.id.etConsideracoes)

        view.findViewById<View>(R.id.btnSolicitarAlteracoes).setOnClickListener {
            if (etConsideracoes.text.isNullOrBlank()) {
                etConsideracoes.error = "Conte o motivo ou sua disponibilidade"
                etConsideracoes.requestFocus()
                return@setOnClickListener
            }
            // TODO: enviar a solicitação para a API (consulta.id + texto)
            Toast.makeText(requireContext(), "Solicitação enviada ao profissional", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        view.findViewById<View>(R.id.btnCancelarConsulta).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cancelar consulta")
                .setMessage("Tem certeza de que deseja cancelar esta consulta?")
                .setNegativeButton("Voltar", null)
                .setPositiveButton("Cancelar consulta") { _, _ ->
                    // TODO: cancelar na API (consulta.id)
                    Toast.makeText(requireContext(), "Consulta cancelada", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .show()
        }
    }
}

/** Abre a tela de reagendar/confirmar empilhando na aba atual. */
fun Fragment.abrirDetalheConsulta(consulta: Consulta) {
    parentFragmentManager.beginTransaction()
        .replace(R.id.fragmentContainer, FragmentConsultaDetalhe.newInstance(consulta))
        .addToBackStack(null)
        .commit()
}
