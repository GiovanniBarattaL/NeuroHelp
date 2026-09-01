package com.example.neurohelp

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neurohelp.agenda.AgendaViewModel
import com.example.neurohelp.agenda.CalendarioAdapter
import com.example.neurohelp.agenda.Consulta
import com.example.neurohelp.agenda.ConsultasAdapter
import com.example.neurohelp.agenda.Especialidade
import com.example.neurohelp.agenda.horaFormatada
import com.google.android.material.card.MaterialCardView

class FragmentAgenda : Fragment() {

    private val viewModel: AgendaViewModel by viewModels()

    /** Ponto de ligação com a tela de detalhes quando ela existir. */
    var aoAbrirDetalhes: ((Consulta) -> Unit)? = null

    private lateinit var calendarioAdapter: CalendarioAdapter
    private lateinit var consultasAdapter: ConsultasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragmentagenda,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtAno = view.findViewById<TextView>(R.id.txtAno)
        val txtMesBanner = view.findViewById<TextView>(R.id.txtMesBanner)
        val txtMesCalendario = view.findViewById<TextView>(R.id.txtMesCalendario)
        val txtLembrete = view.findViewById<TextView>(R.id.txtLembrete)
        val cardLembrete = view.findViewById<MaterialCardView>(R.id.cardLembrete)
        val txtEstadoLista = view.findViewById<TextView>(R.id.txtEstadoLista)
        val progresso = view.findViewById<ProgressBar>(R.id.progressAgenda)
        val rvDias = view.findViewById<RecyclerView>(R.id.rvDias)
        val rvConsultas = view.findViewById<RecyclerView>(R.id.rvConsultas)
        val gridLegenda = view.findViewById<GridLayout>(R.id.gridLegenda)

        calendarioAdapter = CalendarioAdapter { data -> viewModel.selecionarDia(data) }
        rvDias.layoutManager = GridLayoutManager(requireContext(), DIAS_POR_SEMANA)
        rvDias.adapter = calendarioAdapter

        consultasAdapter = ConsultasAdapter { consulta -> abrirDetalhes(consulta) }
        rvConsultas.layoutManager = LinearLayoutManager(requireContext())
        rvConsultas.adapter = consultasAdapter

        preencherLegenda(gridLegenda)

        view.findViewById<ImageView>(R.id.btnMesAnterior).setOnClickListener {
            viewModel.mesAnterior()
        }
        view.findViewById<ImageView>(R.id.btnMesProximo).setOnClickListener {
            viewModel.proximoMes()
        }
        view.findViewById<ImageView>(R.id.imgPerfil).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentPerfil())
                .addToBackStack(null)
                .commit()
        }

        viewModel.ano.observe(viewLifecycleOwner) { txtAno.text = it }
        viewModel.tituloMes.observe(viewLifecycleOwner) { titulo ->
            txtMesCalendario.text = titulo
            txtMesBanner.text = titulo.substringBefore(' ')
        }
        viewModel.dias.observe(viewLifecycleOwner) { calendarioAdapter.submitList(it) }
        viewModel.carregando.observe(viewLifecycleOwner) { carregando ->
            progresso.visibility = if (carregando) View.VISIBLE else View.GONE
        }
        viewModel.proximaConsulta.observe(viewLifecycleOwner) { consulta ->
            if (consulta == null) {
                cardLembrete.visibility = View.GONE
            } else {
                cardLembrete.visibility = View.VISIBLE
                txtLembrete.text = getString(
                    R.string.agenda_lembrete_descricao,
                    consulta.inicio.horaFormatada(),
                    consulta.profissional
                )
            }
        }
        viewModel.itens.observe(viewLifecycleOwner) { itens ->
            consultasAdapter.submitList(itens)
            txtEstadoLista.setText(
                if (itens.isEmpty()) R.string.agenda_sem_consultas
                else R.string.agenda_sem_mais_consultas
            )
        }
        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            if (erro) txtEstadoLista.setText(R.string.agenda_erro)
        }
    }

    private fun preencherLegenda(grid: GridLayout) {
        val inflater = LayoutInflater.from(requireContext())
        grid.removeAllViews()

        Especialidade.entries.forEach { especialidade ->
            val item = inflater.inflate(R.layout.item_legenda_profissional, grid, false)
            item.findViewById<View>(R.id.viewCorLegenda).backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), especialidade.corRes)
                )
            item.findViewById<TextView>(R.id.txtNomeLegenda)
                .setText(especialidade.nomeRes)
            grid.addView(item)
        }
    }

    private fun abrirDetalhes(consulta: Consulta) {
        val acao = aoAbrirDetalhes
        if (acao != null) {
            acao(consulta)
            return
        }
        Toast.makeText(
            requireContext(),
            getString(
                R.string.agenda_titulo_consulta,
                getString(consulta.especialidade.nomeRes)
            ),
            Toast.LENGTH_SHORT
        ).show()
    }

    private companion object {
        const val DIAS_POR_SEMANA = 7
    }
}
