package com.example.neurohelp.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.neurohelp.R
import com.example.neurohelp.agenda.AgendaViewModel
import com.example.neurohelp.agenda.Consulta
import com.example.neurohelp.agenda.dataPorExtenso
import com.example.neurohelp.agenda.horaFormatada
import com.example.neurohelp.configurarCabecalhoPadrao
import com.example.neurohelp.limitarLargura

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

        // Os 3 cards de conteúdo também levam à página de aprendizagem
        listOf(R.id.cardConteudoTea, R.id.cardConteudoEstrategias, R.id.cardConteudoEscola)
            .forEach { id -> view.findViewById<View>(id).setOnClickListener { abrirAprendizagem() } }

        configurarLayoutResponsivo(view)

        // Próximas consultas > "Ver agenda" -> aba Agenda
        view.findViewById<View>(R.id.txtVerAgenda).setOnClickListener { irParaAba(R.id.nav_agenda) }

        configurarProximaConsulta(view)
    }

    private companion object {
        /** Largura máxima do conteúdo em tablets (acima disso sobra margem dos lados). */
        const val LARGURA_MAX_CONTEUDO_DP = 640
    }

    /**
     * Ajustes que o XML sozinho não consegue fazer:
     * 1) tablets/paisagem: limita a largura do bloco abaixo do banner (o banner continua de ponta a ponta);
     * 2) telas estreitas ou fonte do sistema grande: os 3 cards viram uma coluna, para o texto não
     *    ser quebrado no meio das palavras.
     */
    private fun configurarLayoutResponsivo(view: View) {
        val larguraMaxPx = (LARGURA_MAX_CONTEUDO_DP * resources.displayMetrics.density).toInt()
        view.findViewById<View>(R.id.blocoConteudo).limitarLargura(larguraMaxPx)

        val larguraDp = resources.configuration.screenWidthDp
        val fonteGrande = resources.configuration.fontScale > 1.15f
        if (larguraDp < 340 || (fonteGrande && larguraDp < 480)) {
            empilharCardsDeConteudo(view.findViewById(R.id.listaConteudos))
        }
    }

    /** Troca a linha de 3 cards por uma coluna (cada card ocupa a largura toda). */
    private fun empilharCardsDeConteudo(lista: LinearLayout) {
        val espaco = (10 * resources.displayMetrics.density).toInt()
        lista.orientation = LinearLayout.VERTICAL
        for (i in 0 until lista.childCount) {
            val card = lista.getChildAt(i)
            card.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { if (i < lista.childCount - 1) bottomMargin = espaco }
        }
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
