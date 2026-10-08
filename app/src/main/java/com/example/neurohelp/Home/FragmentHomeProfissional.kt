package com.example.neurohelp.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.neurohelp.R
import com.example.neurohelp.agenda.AgendaViewModel
import com.example.neurohelp.agenda.Consulta
import com.example.neurohelp.agenda.dataPorExtenso
import com.example.neurohelp.agenda.horaFormatada
import com.example.neurohelp.configurarCabecalhoPadrao

/**
 * Home de quem entrou como PROFISSIONAL (a do responsável é a FragmentHome).
 * A PrincipalActivity escolhe qual das duas abrir conforme o papel salvo no login.
 */
class FragmentHomeProfissional : Fragment() {

    /** Mesma fonte de dados da Agenda: usada para achar a próxima consulta. */
    private val agendaViewModel: AgendaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.home_profissional_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cabeçalho padrão (a Home é a tela raiz, então não mostra a seta de voltar)
        configurarCabecalhoPadrao(view, mostrarVoltar = false)

        // TODO: trocar "XXX" pelo nome do profissional quando a API informar
        // view.findViewById<TextView>(R.id.txtBoasVindas).text = "Olá, Dr.(a) ${nome}"

        // Card "Aprendizagem" -> página de aprendizagem do site
        view.findViewById<View>(R.id.cardAprendizagem).setOnClickListener { abrirAprendizagem() }

        // Conteúdos de Aprendizagem > "Ver todos" -> mesmo destino do card Aprendizagem
        view.findViewById<View>(R.id.txtVerTodos).setOnClickListener { abrirAprendizagem() }

        // Próximas consultas > "Ver agenda" -> aba Agenda
        view.findViewById<View>(R.id.txtVerAgenda).setOnClickListener { irParaAba(R.id.nav_agenda) }

        configurarProximaConsulta(view)
    }

    private fun configurarProximaConsulta(view: View) {
        val card = view.findViewById<View>(R.id.cardProximaConsulta)

        // Resultado do bottom sheet (igual ao da Agenda e das Notificações)
        ouvirResultadoDetalhesConsulta()

        agendaViewModel.proximaConsulta.observe(viewLifecycleOwner) { consulta ->
            if (consulta == null) {
                card.visibility = View.GONE
                return@observe
            }
            card.visibility = View.VISIBLE
            preencherCardConsulta(view, consulta)

            // Seta e "Ver detalhe da consulta" abrem o mesmo bottom sheet de detalhes
            val abrir = View.OnClickListener { abrirDetalhesConsultaSheet(consulta) }
            view.findViewById<View>(R.id.txtDetalhesConsulta).setOnClickListener(abrir)
            view.findViewById<View>(R.id.imgSetaConsulta).setOnClickListener(abrir)
        }
    }

    private fun preencherCardConsulta(view: View, consulta: Consulta) {
        view.findViewById<TextView>(R.id.txtNomePaciente).text =
            if (consulta.paciente.isNullOrBlank()) "Paciente" else "Paciente ${consulta.paciente}"
        view.findViewById<TextView>(R.id.txtCpfPaciente).apply {
            text = "CPF ${consulta.pacienteCpf.orEmpty()}"
            visibility = if (consulta.pacienteCpf.isNullOrBlank()) View.GONE else View.VISIBLE
        }
        view.findViewById<TextView>(R.id.txtDataConsulta).text = consulta.inicio.dataPorExtenso()
        view.findViewById<TextView>(R.id.txtHorarioConsulta).text = consulta.inicio.horaFormatada()
    }
}
