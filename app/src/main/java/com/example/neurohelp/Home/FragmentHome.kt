package com.example.neurohelp.Home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.neurohelp.PrincipalActivity
import com.example.neurohelp.Profissionais.FragmentPerfilProfissional
import com.example.neurohelp.R
import com.example.neurohelp.agenda.AgendaViewModel
import com.example.neurohelp.agenda.Consulta
import com.example.neurohelp.agenda.DetalhesConsultaDialogFragment
import com.example.neurohelp.agenda.abrirDetalheConsulta
import com.example.neurohelp.agenda.dataPorExtenso
import com.example.neurohelp.agenda.horaFormatada
import com.example.neurohelp.agenda.toConsulta
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

        agendaViewModel.proximaConsulta.observe(viewLifecycleOwner) { consulta ->
            if (consulta == null) {
                card.visibility = View.GONE
                return@observe
            }
            card.visibility = View.VISIBLE
            preencherCardConsulta(view, consulta)

            // "Ver detalhe da consulta" -> bottom sheet de detalhes
            view.findViewById<View>(R.id.txtDetalhesConsulta).setOnClickListener {
                abrirDetalhesConsulta(consulta)
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

    private fun abrirDetalhesConsulta(consulta: Consulta) {
        // Evita abrir o sheet duas vezes com toques rápidos
        if (parentFragmentManager.findFragmentByTag(DetalhesConsultaDialogFragment.TAG) != null) return

        DetalhesConsultaDialogFragment.newInstance(consulta)
            .show(parentFragmentManager, DetalhesConsultaDialogFragment.TAG)
    }

    /** O clique vale para o ícone e também para o texto embaixo dele. */
    private fun configurarAtalho(icone: View, acao: () -> Unit) {
        icone.setOnClickListener { acao() }
        (icone.parent as? View)?.setOnClickListener { acao() }
    }

    /** Troca de aba pelo menu de baixo, então o ícone verde acompanha. */
    private fun irParaAba(itemId: Int) {
        (requireActivity() as? PrincipalActivity)?.irParaAba(itemId)
    }

    private fun abrirAprendizagem() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, URL_APRENDIZAGEM.toUri()))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                R.string.home_erro_abrir_link,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private companion object {
        const val URL_APRENDIZAGEM =
            "https://espectro-care.onrender.com/pages/aprendizagem.html"
    }
}
