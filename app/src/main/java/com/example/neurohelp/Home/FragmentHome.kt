package com.example.neurohelp.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.neurohelp.Profissionais.FragmentPerfilProfissional
import com.example.neurohelp.R
import com.example.neurohelp.agenda.AgendaViewModel
import com.example.neurohelp.agenda.Consulta
import com.example.neurohelp.agenda.dataPorExtenso
import com.example.neurohelp.agenda.horaFormatada
import com.example.neurohelp.configurarCabecalhoPadrao

class FragmentHome : Fragment() {

    /** Mesma fonte de dados da Agenda: usada para achar a próxima consulta. */
    private val agendaViewModel: AgendaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.home_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Cabeçalho padrão (a Home é a tela raiz, então não mostra a seta de voltar)
        configurarCabecalhoPadrao(view, mostrarVoltar = false)

        // Encontrar profissionais -> aba Profissionais
        configurarAtalho(view.findViewById(R.id.btnProfissionais)) {
            irParaAba(R.id.nav_profissionais)
        }

        // Agendar consulta -> aba Agenda
        configurarAtalho(view.findViewById(R.id.btnAgendar)) {
            irParaAba(R.id.nav_agenda)
        }

        // Aprendizagem -> página de aprendizagem do site
        configurarAtalho(view.findViewById(R.id.btnAprendizagem)) {
            abrirAprendizagem()
        }

        // Conteúdos para você > "Ver todos" -> mesmo destino do botão Aprendizagem
        view.findViewById<View>(R.id.txtVerTodos).setOnClickListener {
            abrirAprendizagem()
        }

        // Próximas consultas > "Ver agenda" -> aba Agenda
        view.findViewById<View>(R.id.txtVerAgenda).setOnClickListener {
            irParaAba(R.id.nav_agenda)
        }

        configurarProximaConsulta(view)
    }

    /**
     * Preenche o card "Próximas consultas" com a próxima consulta da agenda e
     * abre o mesmo bottom sheet de detalhes usado na Agenda e nas Notificações.
     */
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

            // "Ver detalhe da consulta" -> bottom sheet de detalhes
            view.findViewById<View>(R.id.txtDetalhesConsulta).setOnClickListener {
                abrirDetalhesConsultaSheet(consulta)
            }
            // Seta -> perfil do profissional (tela já existente)
            view.findViewById<View>(R.id.imgSetaConsulta).setOnClickListener {
                abrirPerfilProfissional(consulta)
            }
        }
    }

    private fun preencherCardConsulta(view: View, consulta: Consulta) {
        view.findViewById<TextView>(R.id.txtNomeProfissional).text = consulta.profissional
        view.findViewById<TextView>(R.id.txtProfissao).text =
            getString(consulta.especialidade.nomeRes)
        view.findViewById<TextView>(R.id.txtCrp).apply {
            text = consulta.crp.orEmpty()
            visibility = if (consulta.crp.isNullOrBlank()) View.GONE else View.VISIBLE
        }
        view.findViewById<TextView>(R.id.txtDataConsulta).text = consulta.inicio.dataPorExtenso()
        view.findViewById<TextView>(R.id.txtHorarioConsulta).text = consulta.inicio.horaFormatada()
    }

    private fun abrirPerfilProfissional(consulta: Consulta) {
        // TODO: a API deve preencher consulta.profissionalId; por enquanto usa o profissional de exemplo (1)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, FragmentPerfilProfissional.newInstance(consulta.profissionalId ?: 1))
            .addToBackStack(null)
            .commit()
    }
}
