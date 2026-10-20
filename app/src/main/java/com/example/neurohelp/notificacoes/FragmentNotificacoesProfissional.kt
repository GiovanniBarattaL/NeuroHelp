package com.example.neurohelp.notificacoes

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neurohelp.R
import com.example.neurohelp.configurarCabecalhoPadrao

/**
 * Notificações de quem entrou como PROFISSIONAL (Figma "Group 235").
 * Usa o mesmo layout de abas (Não Visto / Essa Semana / Todos) da tela do responsável,
 * mas com os avisos dos pacientes. Tocar num card abre o pop-up com o detalhe.
 */
class FragmentNotificacoesProfissional : Fragment() {

    private var filtro = FiltroNotificacao.NAO_VISTO
    private lateinit var adapter: NotificacoesProfissionalAdapter
    private lateinit var txtVazio: TextView
    private lateinit var abas: Map<FiltroNotificacao, Pair<TextView, View>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.notificacoes_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarCabecalhoPadrao(view, abrirSino = false)

        txtVazio = view.findViewById(R.id.txtSemNotificacoes)

        adapter = NotificacoesProfissionalAdapter { abrirNotificacao(it) }
        view.findViewById<RecyclerView>(R.id.rvNotificacoes).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FragmentNotificacoesProfissional.adapter
        }

        abas = mapOf(
            FiltroNotificacao.NAO_VISTO to Pair(
                view.findViewById<TextView>(R.id.txtAbaNaoVisto),
                view.findViewById<View>(R.id.linhaAbaNaoVisto)
            ),
            FiltroNotificacao.ESSA_SEMANA to Pair(
                view.findViewById<TextView>(R.id.txtAbaEssaSemana),
                view.findViewById<View>(R.id.linhaAbaEssaSemana)
            ),
            FiltroNotificacao.TODOS to Pair(
                view.findViewById<TextView>(R.id.txtAbaTodos),
                view.findViewById<View>(R.id.linhaAbaTodos)
            )
        )
        view.findViewById<View>(R.id.abaNaoVisto).setOnClickListener { selecionar(FiltroNotificacao.NAO_VISTO) }
        view.findViewById<View>(R.id.abaEssaSemana).setOnClickListener { selecionar(FiltroNotificacao.ESSA_SEMANA) }
        view.findViewById<View>(R.id.abaTodos).setOnClickListener { selecionar(FiltroNotificacao.TODOS) }

        selecionar(filtro)
    }

    private fun selecionar(novo: FiltroNotificacao) {
        filtro = novo
        abas.forEach { (tipo, par) ->
            val (texto, linha) = par
            val ativa = tipo == novo
            texto.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (ativa) R.color.agenda_verde_escuro else R.color.agenda_texto_secundario
                )
            )
            linha.setBackgroundColor(
                if (ativa) ContextCompat.getColor(requireContext(), R.color.agenda_verde_escuro)
                else Color.TRANSPARENT
            )
        }
        mostrar(NotificacoesProfissionalStore.filtrar(novo))
    }

    private fun mostrar(itens: List<NotificacaoProfissional>) {
        adapter.submitList(itens)
        txtVazio.visibility = if (itens.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun abrirNotificacao(item: NotificacaoProfissional) {
        NotificacoesProfissionalStore.marcarComoVista(item.id)

        // Mantém o card na lista enquanto o pop-up está aberto;
        // na próxima troca de aba a lista é filtrada de novo.
        mostrar(adapter.currentList.map { if (it.id == item.id) it.copy(visto = true) else it })

        DetalheNotificacaoProfissionalDialogFragment.newInstance(item.id)
            .show(parentFragmentManager, DetalheNotificacaoProfissionalDialogFragment.TAG)
    }
}
