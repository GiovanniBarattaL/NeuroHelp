package com.example.neurohelp.Profissionais

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.Fragment
import com.example.neurohelp.Perfil.FragmentPerfil
import com.example.neurohelp.R
import com.example.neurohelp.notificacoes.abrirNotificacoes
import java.util.Locale

class FragmentPerfilProfissional : Fragment() {

    private lateinit var profissional: ProfissionalPerfil

    private var favoritado = false
    private var avaliacoesExibidas = 0

    companion object {
        private const val ARG_ID = "profissional_id"
        private const val AVALIACOES_POR_PAGINA = 2

        fun newInstance(profissionalId: Int) = FragmentPerfilProfissional().apply {
            arguments = bundleOf(ARG_ID to profissionalId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.profissionais_perfil,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getInt(ARG_ID, 0) ?: 0
        profissional = ProfissionalMock.exemplo(id) // TODO: buscar na API pelo id

        configurarCabecalho(view)
        preencherDados(view)
        configurarFavoritar(view)
        configurarAgendar(view)
        configurarAvaliacoes(view)
    }

    // ---------------------------------------------------------------
    // Cabeçalho (voltar + sino + avatar)
    // ---------------------------------------------------------------
    private fun configurarCabecalho(view: View) {
        view.findViewById<ImageView>(R.id.imgVoltar).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<ImageView>(R.id.imgNotificacao).setOnClickListener {
            abrirNotificacoes()
        }

        view.findViewById<ImageView>(R.id.imgPerfil).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentPerfil())
                .addToBackStack(null)
                .commit()
        }
    }

    // ---------------------------------------------------------------
    // Dados do profissional
    // ---------------------------------------------------------------
    private fun preencherDados(view: View) {
        val p = profissional

        view.findViewById<TextView>(R.id.txtNomeProfissional).text = p.nome
        view.findViewById<TextView>(R.id.txtProfissaoProfissional).text = p.profissao
        view.findViewById<TextView>(R.id.txtRegistroProfissional).text = p.registro
        view.findViewById<TextView>(R.id.txtAtendimentoProfissional).text = p.atendimento
        view.findViewById<TextView>(R.id.txtLocalProfissional).text = p.local
        view.findViewById<TextView>(R.id.txtTotalAvaliacoes).text =
            getString(R.string.profissional_total_avaliacoes, p.totalAvaliacoes)

        EstrelasHelper.preencher(
            view.findViewById(R.id.estrelasProfissional),
            p.notaMedia,
            tamanhoDp = 16,
            espacoDp = 2
        )

        view.findViewById<TextView>(R.id.txtAbordagem).text =
            textoComRotulo(getString(R.string.profissional_abordagem), p.abordagem)
        view.findViewById<TextView>(R.id.txtPublicoAlvo).text =
            textoComRotulo(getString(R.string.profissional_publico_alvo), p.publicoAlvo)
        view.findViewById<TextView>(R.id.txtSobreMim).text =
            textoComRotulo(getString(R.string.profissional_sobre_mim), p.sobreMim)

        // Nota média "3,5 de 5"
        view.findViewById<TextView>(R.id.txtNotaMedia).text =
            String.format(Locale("pt", "BR"), "%.1f", p.notaMedia)
        EstrelasHelper.preencher(
            view.findViewById(R.id.estrelasResumo),
            p.notaMedia,
            tamanhoDp = 15,
            espacoDp = 2
        )
    }

    /** "Rótulo:" em negrito seguido do texto normal. */
    private fun textoComRotulo(rotulo: String, texto: String) = buildSpannedString {
        bold { append("$rotulo ") }
        append(texto)
    }

    // ---------------------------------------------------------------
    // Favoritar
    // ---------------------------------------------------------------
    private fun configurarFavoritar(view: View) {
        val btnFavoritar = view.findViewById<LinearLayout>(R.id.btnFavoritar)
        val txtFavoritar = view.findViewById<TextView>(R.id.txtFavoritar)
        val imgFavoritar = view.findViewById<ImageView>(R.id.imgFavoritar)

        btnFavoritar.setOnClickListener {
            favoritado = !favoritado

            if (favoritado) {
                txtFavoritar.text = getString(R.string.profissional_favoritado)
                imgFavoritar.setImageResource(R.drawable.ic_favorito_cheio)
            } else {
                txtFavoritar.text = getString(R.string.profissional_favoritar)
                imgFavoritar.setImageResource(R.drawable.ic_favorito)
            }
            // TODO: salvar/remover o favorito na API/Firestore/Room
        }
    }

    // ---------------------------------------------------------------
    // Agendar consulta (abre o WhatsApp do profissional)
    // ---------------------------------------------------------------
    private fun configurarAgendar(view: View) {
        view.findViewById<View>(R.id.btnAgendarConsulta).setOnClickListener {
            val mensagem = getString(R.string.profissional_mensagem_whatsapp, profissional.nome)
            val uri = Uri.parse(
                "https://wa.me/${profissional.whatsapp}?text=${Uri.encode(mensagem)}"
            )

            try {
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.profissional_whatsapp_indisponivel),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ---------------------------------------------------------------
    // Avaliações + "Ver mais"
    // ---------------------------------------------------------------
    private fun configurarAvaliacoes(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.containerAvaliacoes)
        val btnVerMais = view.findViewById<TextView>(R.id.btnVerMais)

        fun mostrarMais() {
            val lista = profissional.avaliacoes
            val fim = minOf(avaliacoesExibidas + AVALIACOES_POR_PAGINA, lista.size)

            for (i in avaliacoesExibidas until fim) {
                container.addView(criarItemAvaliacao(container, lista[i]))
            }
            avaliacoesExibidas = fim

            btnVerMais.visibility =
                if (avaliacoesExibidas >= lista.size) View.GONE else View.VISIBLE
        }

        btnVerMais.setOnClickListener { mostrarMais() }

        // Mostra as primeiras avaliações (só na primeira vez que a tela é criada)
        container.removeAllViews()
        avaliacoesExibidas = 0
        mostrarMais()

        // TODO: enviar o comentário digitado em R.id.edtComentario (actionSend)
        // TODO: aplicar os filtros das avaliações em R.id.btnFiltrosAvaliacoes
    }

    private fun criarItemAvaliacao(pai: ViewGroup, avaliacao: Avaliacao): View {
        val item = layoutInflater.inflate(R.layout.profissionais_item_avaliacao, pai, false)

        item.findViewById<TextView>(R.id.txtNomeAvaliador).text = avaliacao.nome
        item.findViewById<TextView>(R.id.txtDataAvaliacao).text = avaliacao.data
        item.findViewById<TextView>(R.id.txtTextoAvaliacao).text = avaliacao.texto

        EstrelasHelper.preencher(
            item.findViewById(R.id.estrelasAvaliacao),
            avaliacao.nota,
            tamanhoDp = 12,
            espacoDp = 1
        )

        return item
    }
}
