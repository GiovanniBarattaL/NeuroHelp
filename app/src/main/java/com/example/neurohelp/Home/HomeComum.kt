package com.example.neurohelp.Home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.neurohelp.PrincipalActivity
import com.example.neurohelp.R
import com.example.neurohelp.agenda.Consulta
import com.example.neurohelp.agenda.DetalhesConsultaDialogFragment
import com.example.neurohelp.agenda.DetalhesConsultaProfissionalDialogFragment
import com.example.neurohelp.agenda.abrirEdicaoConsulta
import com.example.neurohelp.auth.ehProfissional
import com.example.neurohelp.agenda.abrirDetalheConsulta
import com.example.neurohelp.agenda.toConsulta

// Pedaços usados tanto pela Home do responsável quanto pela Home do profissional.

private const val URL_APRENDIZAGEM = "https://espectro-care.onrender.com/pages/aprendizagem.html"

/** Troca de aba pelo menu de baixo, então o ícone verde acompanha. */
fun Fragment.irParaAba(itemId: Int) {
    (requireActivity() as? PrincipalActivity)?.irParaAba(itemId)
}

/** Abre a página de aprendizagem do site (botão Aprendizagem e "Ver todos"). */
fun Fragment.abrirAprendizagem() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, URL_APRENDIZAGEM.toUri()))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(requireContext(), R.string.home_erro_abrir_link, Toast.LENGTH_SHORT).show()
    }
}

/** O clique vale para o ícone e também para o texto embaixo dele. */
fun configurarAtalho(icone: View, acao: () -> Unit) {
    icone.setOnClickListener { acao() }
    (icone.parent as? View)?.setOnClickListener { acao() }
}

/**
 * Abre o bottom sheet de detalhes da consulta: o do responsável (igual ao das Notificações)
 * ou, para quem entrou como profissional, o com os dados do paciente.
 */
fun Fragment.abrirDetalhesConsultaSheet(consulta: Consulta) {
    // Evita abrir o sheet duas vezes com toques rápidos
    if (parentFragmentManager.findFragmentByTag(DetalhesConsultaDialogFragment.TAG) != null ||
        parentFragmentManager.findFragmentByTag(DetalhesConsultaProfissionalDialogFragment.TAG) != null
    ) return

    if (ehProfissional()) {
        DetalhesConsultaProfissionalDialogFragment.newInstance(consulta)
            .show(parentFragmentManager, DetalhesConsultaProfissionalDialogFragment.TAG)
    } else {
        DetalhesConsultaDialogFragment.newInstance(consulta)
            .show(parentFragmentManager, DetalhesConsultaDialogFragment.TAG)
    }
}

/**
 * Trata os botões dos bottom sheets de detalhes:
 * responsável ("Confirmar consulta" / "Alterar ou cancelar") e
 * profissional ("Editar Consulta" / "Cancelar consulta").
 */
fun Fragment.ouvirResultadoDetalhesConsulta() {
    parentFragmentManager.setFragmentResultListener(
        DetalhesConsultaProfissionalDialogFragment.REQUEST_KEY,
        viewLifecycleOwner
    ) { _, resultado ->
        when (resultado.getString(DetalhesConsultaProfissionalDialogFragment.EXTRA_ACAO)) {
            DetalhesConsultaProfissionalDialogFragment.ACAO_EDITAR ->
                abrirEdicaoConsulta(resultado.toConsulta())
            DetalhesConsultaProfissionalDialogFragment.ACAO_CANCELAR ->
                confirmarCancelamentoConsulta()
        }
    }

    parentFragmentManager.setFragmentResultListener(
        DetalhesConsultaDialogFragment.REQUEST_KEY,
        viewLifecycleOwner
    ) { _, resultado ->
        when (resultado.getString(DetalhesConsultaDialogFragment.EXTRA_ACAO)) {
            DetalhesConsultaDialogFragment.ACAO_CONFIRMAR ->
                // TODO: confirmar presença na API
                Toast.makeText(requireContext(), "Consulta confirmada!", Toast.LENGTH_SHORT).show()
            DetalhesConsultaDialogFragment.ACAO_ALTERAR_CANCELAR ->
                abrirDetalheConsulta(resultado.toConsulta())
        }
    }
}

private fun Fragment.confirmarCancelamentoConsulta() {
    AlertDialog.Builder(requireContext())
        .setTitle("Cancelar consulta")
        .setMessage("Tem certeza de que deseja cancelar esta consulta?")
        .setNegativeButton("Voltar", null)
        .setPositiveButton("Cancelar consulta") { _, _ ->
            // TODO: cancelar na API (consulta.id)
            Toast.makeText(requireContext(), "Consulta cancelada", Toast.LENGTH_SHORT).show()
        }
        .show()
}
