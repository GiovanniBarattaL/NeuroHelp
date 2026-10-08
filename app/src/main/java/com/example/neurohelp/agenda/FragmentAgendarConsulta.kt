package com.example.neurohelp.agenda

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.neurohelp.Home.irParaAba
import com.example.neurohelp.R
import com.example.neurohelp.configurarCabecalhoPadrao
import java.util.Calendar

/**
 * Agendar consulta (profissional). Dois modos:
 *  - NOVA: vem de [FragmentSelecionarPaciente]; botão "Agendar consulta".
 *  - EDIÇÃO: vem do botão "Editar Consulta" do bottom sheet; campos já preenchidos e
 *    botão "Confirmar e alterar".
 */
class FragmentAgendarConsulta : Fragment() {

    companion object {
        private const val ARG_PACIENTE_ID = "paciente_id"
        private const val ARG_EDICAO = "modo_edicao"

        private const val ESTADO_TIPO = "estado_tipo"
        private const val ESTADO_FORMATO = "estado_formato"
        private const val ESTADO_DATA = "estado_data"
        private const val ESTADO_HORA = "estado_hora"
        private const val ESTADO_MINUTO = "estado_minuto"

        fun novaConsulta(pacienteId: String) = FragmentAgendarConsulta().apply {
            arguments = bundleOf(ARG_PACIENTE_ID to pacienteId)
        }

        fun editarConsulta(consulta: Consulta) = FragmentAgendarConsulta().apply {
            arguments = consulta.toBundle().apply { putBoolean(ARG_EDICAO, true) }
        }
    }

    private var edicao = false

    private var tipo: Especialidade? = null
    private var formato: ModalidadeConsulta? = null
    private var dia: Calendar? = null   // só ano/mês/dia
    private var hora = -1
    private var minuto = -1

    private lateinit var txtTipo: TextView
    private lateinit var txtFormato: TextView
    private lateinit var txtData: TextView
    private lateinit var txtHorario: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.agenda_agendar_consulta, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarCabecalhoPadrao(view)

        val args = requireArguments()
        edicao = args.getBoolean(ARG_EDICAO, false)
        val consulta = if (edicao) args.toConsulta() else null

        txtTipo = view.findViewById(R.id.txtTipo)
        txtFormato = view.findViewById(R.id.txtFormato)
        txtData = view.findViewById(R.id.txtData)
        txtHorario = view.findViewById(R.id.txtHorario)
        val etObservacoes = view.findViewById<EditText>(R.id.etObservacoes)

        preencherPaciente(view, consulta)

        // Estado inicial: vazio (nova) ou vindo da consulta (edição); depois da rotação, o que estava na tela
        if (savedInstanceState != null) {
            restaurarEstado(savedInstanceState)
        } else if (consulta != null) {
            tipo = consulta.especialidade
            formato = consulta.modalidade
            dia = Calendar.getInstance().apply { time = consulta.inicio }
            hora = dia!!.get(Calendar.HOUR_OF_DAY)
            minuto = dia!!.get(Calendar.MINUTE)
            etObservacoes.setText(consulta.observacoes.orEmpty())
        }
        atualizarCampos()

        view.findViewById<View>(R.id.linhaTipo).setOnClickListener { escolherTipo() }
        view.findViewById<View>(R.id.linhaFormato).setOnClickListener { escolherFormato() }
        view.findViewById<View>(R.id.linhaData).setOnClickListener { escolherData() }
        view.findViewById<View>(R.id.linhaHorario).setOnClickListener { escolherHorario() }

        val btnAgendar = view.findViewById<View>(R.id.btnAgendarConsulta)
        val btnAlterar = view.findViewById<View>(R.id.btnConfirmarAlteracao)
        btnAgendar.visibility = if (edicao) View.GONE else View.VISIBLE
        btnAlterar.visibility = if (edicao) View.VISIBLE else View.GONE

        val confirmar = View.OnClickListener { confirmar(etObservacoes.text.toString().trim()) }
        btnAgendar.setOnClickListener(confirmar)
        btnAlterar.setOnClickListener(confirmar)
    }

    // ---------------------------------------------------------------
    // Paciente + responsável
    // ---------------------------------------------------------------
    private fun preencherPaciente(view: View, consulta: Consulta?) {
        val paciente: Paciente = if (consulta != null) {
            PacientesMock.porNome(consulta.paciente)
                ?: Paciente("", consulta.paciente ?: "—", consulta.pacienteCpf.orEmpty())
        } else {
            PacientesMock.porId(requireArguments().getString(ARG_PACIENTE_ID))
                ?: Paciente("", "—", "")
        }

        view.findViewById<TextView>(R.id.txtNomePaciente).text = "Nome: ${paciente.nome}"
        view.findViewById<TextView>(R.id.txtCpfPaciente).text = "CPF: ${paciente.cpf}"

        val responsavel = paciente.responsavel
        view.findViewById<View>(R.id.blocoResponsavel).visibility =
            if (responsavel == null) View.GONE else View.VISIBLE
        if (responsavel != null) {
            view.findViewById<TextView>(R.id.txtRespNome).text = "Nome: ${responsavel.nome}"
            view.findViewById<TextView>(R.id.txtRespCpf).text = "CPF: ${responsavel.cpf}"
            view.findViewById<TextView>(R.id.txtRespSexo).text = "Sexo: ${responsavel.sexo}"
            view.findViewById<TextView>(R.id.txtRespNasc).text = "Data de Nasc: ${responsavel.nascimento}"
        }
    }

    // ---------------------------------------------------------------
    // Escolhas
    // ---------------------------------------------------------------
    private fun escolherTipo() {
        val opcoes = Especialidade.entries
        val nomes = opcoes.map { getString(it.tipoConsultaRes()) }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.agendar_tipo_consulta)
            .setItems(nomes) { _, i ->
                tipo = opcoes[i]
                atualizarCampos()
            }
            .show()
    }

    private fun escolherFormato() {
        val opcoes = ModalidadeConsulta.entries
        val nomes = opcoes.map { getString(it.nomeRes) }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.agendar_formato_consulta)
            .setItems(nomes) { _, i ->
                formato = opcoes[i]
                atualizarCampos()
            }
            .show()
    }

    private fun escolherData() {
        val base = dia ?: Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, ano, mes, diaDoMes ->
                dia = Calendar.getInstance().apply { clear(); set(ano, mes, diaDoMes) }
                atualizarCampos()
            },
            base.get(Calendar.YEAR),
            base.get(Calendar.MONTH),
            base.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // Não agenda no passado
            datePicker.minDate = System.currentTimeMillis() - 1000
        }.show()
    }

    private fun escolherHorario() {
        val agora = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, h, m ->
                hora = h
                minuto = m
                atualizarCampos()
            },
            if (hora >= 0) hora else agora.get(Calendar.HOUR_OF_DAY),
            if (minuto >= 0) minuto else 0,
            true
        ).show()
    }

    /** Mostra o valor escolhido no lugar do texto da linha (cinza = vazio, azul = preenchido). */
    private fun atualizarCampos() {
        mostrar(txtTipo, tipo?.let { getString(it.tipoConsultaRes()) }, R.string.agendar_tipo_consulta)
        mostrar(txtFormato, formato?.let { getString(it.nomeRes) }, R.string.agendar_formato_consulta)
        mostrar(
            txtData,
            dia?.let {
                String.format(
                    java.util.Locale("pt", "BR"), "%02d/%02d/%04d",
                    it.get(Calendar.DAY_OF_MONTH), it.get(Calendar.MONTH) + 1, it.get(Calendar.YEAR)
                )
            },
            R.string.agendar_data
        )
        mostrar(
            txtHorario,
            if (hora >= 0 && minuto >= 0) String.format(java.util.Locale("pt", "BR"), "%02d:%02d", hora, minuto) else null,
            R.string.agendar_horario
        )
    }

    private fun mostrar(campo: TextView, valor: String?, @StringRes vazio: Int) {
        campo.text = valor ?: getString(vazio)
        campo.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (valor == null) R.color.agenda_texto_secundario else R.color.azul_titulo
            )
        )
    }

    // ---------------------------------------------------------------
    // Confirmar
    // ---------------------------------------------------------------
    private fun confirmar(observacoes: String) {
        if (tipo == null || formato == null || dia == null || hora < 0 || minuto < 0) {
            Toast.makeText(requireContext(), R.string.agendar_preencha_campos, Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: enviar para a API (paciente, tipo, formato, data/hora e observações;
        //       na edição, atualizar a consulta existente em vez de criar outra)
        Toast.makeText(
            requireContext(),
            if (edicao) R.string.agendar_consulta_alterada else R.string.agendar_consulta_agendada,
            Toast.LENGTH_SHORT
        ).show()

        // Volta para a Agenda
        irParaAba(R.id.nav_agenda)
    }

    // ---------------------------------------------------------------
    // Estado (rotação de tela)
    // ---------------------------------------------------------------
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ESTADO_TIPO, tipo?.name)
        outState.putString(ESTADO_FORMATO, formato?.name)
        outState.putLong(ESTADO_DATA, dia?.timeInMillis ?: -1L)
        outState.putInt(ESTADO_HORA, hora)
        outState.putInt(ESTADO_MINUTO, minuto)
    }

    private fun restaurarEstado(estado: Bundle) {
        tipo = estado.getString(ESTADO_TIPO)?.let { Especialidade.valueOf(it) }
        formato = estado.getString(ESTADO_FORMATO)?.let { ModalidadeConsulta.valueOf(it) }
        dia = estado.getLong(ESTADO_DATA, -1L).takeIf { it >= 0 }
            ?.let { Calendar.getInstance().apply { timeInMillis = it } }
        hora = estado.getInt(ESTADO_HORA, -1)
        minuto = estado.getInt(ESTADO_MINUTO, -1)
    }
}

/** Nome do "Tipo de Consulta" para cada especialidade (ex.: Psiquiatra -> "Psiquiatria"). */
@StringRes
fun Especialidade.tipoConsultaRes(): Int = when (this) {
    Especialidade.FONOAUDIOLOGO -> R.string.agendar_tipo_fonoaudiologia
    Especialidade.TERAPEUTA_OCUPACIONAL -> R.string.agendar_tipo_terapia_ocupacional
    Especialidade.PSIQUIATRA -> R.string.agendar_tipo_psiquiatria
    Especialidade.PSICOLOGO -> R.string.agendar_tipo_psicologia
    Especialidade.NEUROPEDIATRA -> R.string.agendar_tipo_neuropediatria
    Especialidade.NUTRICIONISTA -> R.string.agendar_tipo_nutricao
}
