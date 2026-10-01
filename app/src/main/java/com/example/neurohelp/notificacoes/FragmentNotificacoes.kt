package com.example.neurohelp.notificacoes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neurohelp.R
import com.example.neurohelp.agenda.DetalhesConsultaDialogFragment
import com.example.neurohelp.agenda.abrirDetalheConsulta
import com.example.neurohelp.agenda.toConsulta

/** Tela de Notificações (Figma): abas Não Visto / Essa Semana / Todos. */
class FragmentNotificacoes : Fragment() {

    private var filtro = FiltroNotificacao.NAO_VISTO
    private lateinit var adapter: NotificacoesAdapter
    private lateinit var txtVazio: TextView

    private lateinit var abas: Map<FiltroNotificacao, Pair<TextView, View>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.notificacoes_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.imgVoltar).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        txtVazio = view.findViewById(R.id.txtSemNotificacoes)

        adapter = NotificacoesAdapter { abrirNotificacao(it) }
        view.findViewById<RecyclerView>(R.id.rvNotificacoes).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FragmentNotificacoes.adapter
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

        // Resultado do bottom sheet de detalhes
        parentFragmentManager.setFragmentResultListener(
            DetalhesConsultaDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, resultado ->
            val consulta = resultado.toConsulta()
            when (resultado.getString(DetalhesConsultaDialogFragment.EXTRA_ACAO)) {
                DetalhesConsultaDialogFragment.ACAO_CONFIRMAR -> {
                    // TODO: confirmar presença na API (consulta.id)
                    Toast.makeText(requireContext(), "Consulta confirmada!", Toast.LENGTH_SHORT).show()
                }
                DetalhesConsultaDialogFragment.ACAO_ALTERAR_CANCELAR -> abrirDetalheConsulta(consulta)
            }
        }

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
                else android.graphics.Color.TRANSPARENT
            )
        }
        mostrar(NotificacoesStore.filtrar(novo))
    }

    private fun mostrar(itens: List<Notificacao>) {
        adapter.submitList(itens)
        txtVazio.visibility = if (itens.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun abrirNotificacao(notificacao: Notificacao) {
        NotificacoesStore.marcarComoVista(notificacao.id)

        // Mantém o card na lista (agora com avatar verde) enquanto o sheet está aberto;
        // na próxima troca de aba a lista é filtrada de novo.
        mostrar(adapter.currentList.map { if (it.id == notificacao.id) it.copy(visto = true) else it })

        DetalhesConsultaDialogFragment.newInstance(notificacao.consulta)
            .show(parentFragmentManager, DetalhesConsultaDialogFragment.TAG)
    }
}

/** Abre a tela de Notificações empilhando na aba atual (usada pelo sino do cabeçalho). */
fun Fragment.abrirNotificacoes() {
    parentFragmentManager.beginTransaction()
        .replace(R.id.fragmentContainer, FragmentNotificacoes())
        .addToBackStack(null)
        .commit()
}
