package com.example.neurohelp.agenda

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.neurohelp.R
import java.util.Calendar
import java.util.Date

private val MESES = arrayOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
)

private val DIAS_SEMANA = arrayOf(
    "Domingo", "Segunda-Feira", "Terça-Feira", "Quarta-Feira",
    "Quinta-Feira", "Sexta-Feira", "Sábado"
)

/** Ex.: "01 de Abril de 2026" */
fun Date.dataPorExtenso(): String {
    val c = Calendar.getInstance().apply { time = this@dataPorExtenso }
    return String.format(
        java.util.Locale("pt", "BR"),
        "%02d de %s de %d",
        c.get(Calendar.DAY_OF_MONTH),
        MESES[c.get(Calendar.MONTH)],
        c.get(Calendar.YEAR)
    )
}

/** Ex.: "Terça-Feira" */
fun Date.diaDaSemana(): String {
    val c = Calendar.getInstance().apply { time = this@diaDaSemana }
    return DIAS_SEMANA[c.get(Calendar.DAY_OF_WEEK) - 1]
}

// ---------------------------------------------------------------
// Consulta <-> Bundle (para passar entre fragments/dialogs)
// ---------------------------------------------------------------
private const val K_ID = "consulta_id"
private const val K_ESPECIALIDADE = "consulta_especialidade"
private const val K_PROFISSIONAL = "consulta_profissional"
private const val K_INICIO = "consulta_inicio"
private const val K_MODALIDADE = "consulta_modalidade"
private const val K_LOCAL = "consulta_local"
private const val K_CRP = "consulta_crp"
private const val K_SOBRE = "consulta_sobre"
private const val K_OBS = "consulta_obs"
private const val K_FOCO = "consulta_foco"
private const val K_PROF_ID = "consulta_profissional_id"
private const val K_PACIENTE = "consulta_paciente"
private const val K_PACIENTE_CPF = "consulta_paciente_cpf"
private const val K_SOBRE_PACIENTE = "consulta_sobre_paciente"

fun Consulta.toBundle(): Bundle = Bundle().apply {
    putString(K_ID, id)
    putString(K_ESPECIALIDADE, especialidade.name)
    putString(K_PROFISSIONAL, profissional)
    putLong(K_INICIO, inicio.time)
    putString(K_MODALIDADE, modalidade.name)
    putString(K_LOCAL, local)
    putString(K_CRP, crp)
    putString(K_SOBRE, sobreProfissional)
    putString(K_OBS, observacoes)
    putString(K_FOCO, foco)
    profissionalId?.let { putInt(K_PROF_ID, it) }
    putString(K_PACIENTE, paciente)
    putString(K_PACIENTE_CPF, pacienteCpf)
    putString(K_SOBRE_PACIENTE, sobrePaciente)
}

fun Bundle.toConsulta(): Consulta = Consulta(
    id = getString(K_ID).orEmpty(),
    especialidade = Especialidade.valueOf(getString(K_ESPECIALIDADE) ?: Especialidade.PSICOLOGO.name),
    profissional = getString(K_PROFISSIONAL).orEmpty(),
    inicio = Date(getLong(K_INICIO)),
    modalidade = ModalidadeConsulta.valueOf(getString(K_MODALIDADE) ?: ModalidadeConsulta.ONLINE.name),
    local = getString(K_LOCAL),
    crp = getString(K_CRP),
    sobreProfissional = getString(K_SOBRE),
    observacoes = getString(K_OBS),
    foco = getString(K_FOCO),
    profissionalId = if (containsKey(K_PROF_ID)) getInt(K_PROF_ID) else null,
    paciente = getString(K_PACIENTE),
    pacienteCpf = getString(K_PACIENTE_CPF),
    sobrePaciente = getString(K_SOBRE_PACIENTE)
)

/**
 * Preenche as views de detalhe da consulta. Serve para o bottom sheet
 * (agenda_dialog_consulta_detalhes) e para a tela agenda_reagendar_consulta,
 * que usam os mesmos ids.
 */
fun View.preencherDetalhesConsulta(consulta: Consulta) {
    val nomeEspecialidade = context.getString(consulta.especialidade.nomeRes)

    fun texto(id: Int) = findViewById<TextView>(id)

    texto(R.id.txtTitulo).text =
        context.getString(R.string.agenda_titulo_consulta, nomeEspecialidade)
    texto(R.id.txtNome).text = consulta.profissional
    texto(R.id.txtProfissao).text = nomeEspecialidade

    texto(R.id.txtCrp).apply {
        text = consulta.crp.orEmpty()
        visibility = if (consulta.crp.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    texto(R.id.txtData).text = "${consulta.inicio.dataPorExtenso()}\n(${consulta.inicio.diaDaSemana()})"
    texto(R.id.txtHorario).text = consulta.inicio.horaFormatada()
    texto(R.id.txtFormato).text = context.getString(consulta.modalidade.nomeRes)
    texto(R.id.txtStatus).text = "Confirmada"

    texto(R.id.txtSobreProfissional).text = consulta.sobreProfissional.orEmpty()

    texto(R.id.txtObservacoes).apply {
        text = consulta.observacoes.orEmpty()
        visibility = if (consulta.observacoes.isNullOrBlank()) View.GONE else View.VISIBLE
    }
    texto(R.id.txtFoco).apply {
        text = "Foco: ${consulta.foco.orEmpty()}"
        visibility = if (consulta.foco.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    // Círculo do profissional na cor da especialidade
    findViewById<View>(R.id.frameProfissional).backgroundTintList =
        ColorStateList.valueOf(ContextCompat.getColor(context, consulta.especialidade.corRes))
}
