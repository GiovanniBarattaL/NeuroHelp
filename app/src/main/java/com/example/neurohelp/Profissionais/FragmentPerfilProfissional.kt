package com.example.neurohelp.Profissionais

import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.Fragment
import com.example.neurohelp.R
import com.example.neurohelp.configurarCabecalhoPadrao
import java.util.Locale

class FragmentPerfilProfissional : Fragment() {

    private lateinit var profissional: ProfissionalPerfil

    private var favoritado = false
    private var avaliacoesExibidas = 0

    /** Notas (1 a 5) marcadas no filtro das avaliações. Vazio = sem filtro. */
    private var notasFiltradas: Set<Int> = emptySet()
    private var popupFiltros: PopupWindow? = null
    private var telaPerfil: View? = null

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
        telaPerfil = view

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
        configurarCabecalhoPadrao(view)
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
            abrirWhatsAppProfissional(profissional.nome, profissional.whatsapp)
        }
    }

    // ---------------------------------------------------------------
    // Avaliações + "Ver mais"
    // ---------------------------------------------------------------
    private fun configurarAvaliacoes(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.containerAvaliacoes)
        val btnVerMais = view.findViewById<TextView>(R.id.btnVerMais)

        fun mostrarMais() {
            val lista = avaliacoesFiltradas()
            val fim = minOf(avaliacoesExibidas + AVALIACOES_POR_PAGINA, lista.size)

            for (i in avaliacoesExibidas until fim) {
                container.addView(criarItemAvaliacao(container, lista[i]))
            }
            avaliacoesExibidas = fim

            btnVerMais.visibility =
                if (avaliacoesExibidas >= lista.size) View.GONE else View.VISIBLE
        }

        // Recomeça a lista do zero (primeira exibição e sempre que o filtro muda)
        fun recarregar() {
            container.removeAllViews()
            avaliacoesExibidas = 0

            if (avaliacoesFiltradas().isEmpty()) {
                container.addView(criarAvisoSemAvaliacoes(container))
                btnVerMais.visibility = View.GONE
            } else {
                mostrarMais()
            }
        }

        btnVerMais.setOnClickListener { mostrarMais() }

        view.findViewById<View>(R.id.btnFiltrosAvaliacoes).setOnClickListener { botao ->
            abrirFiltrosAvaliacoes(botao) { recarregar() }
        }

        recarregar()

        // TODO: enviar o comentário digitado em R.id.edtComentario (actionSend)
    }

    private fun avaliacoesFiltradas(): List<Avaliacao> =
        if (notasFiltradas.isEmpty()) profissional.avaliacoes
        else profissional.avaliacoes.filter { it.nota.toInt() in notasFiltradas }

    private fun criarAvisoSemAvaliacoes(pai: ViewGroup): View =
        TextView(requireContext()).apply {
            text = getString(R.string.profissional_sem_avaliacoes)
            setTextColor(Color.parseColor("#6B6B6B"))
            textSize = 12f
            typeface = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), R.font.nunito)
            setPadding(0, dp(12), 0, dp(12))
        }

    // ---------------------------------------------------------------
    // Filtro das avaliações (mesmo painel do filtro de Profissionais)
    // ---------------------------------------------------------------
    private fun abrirFiltrosAvaliacoes(ancora: View, aoAplicar: () -> Unit) {
        // Evita abrir dois filtros ao mesmo tempo
        if (popupFiltros?.isShowing == true) return

        val conteudo = layoutInflater.inflate(R.layout.profissionais_filtros_avaliacoes, null)

        val checks = listOf(
            5 to R.id.check5Estrelas,
            4 to R.id.check4Estrelas,
            3 to R.id.check3Estrelas,
            2 to R.id.check2Estrelas,
            1 to R.id.check1Estrela
        ).map { (nota, idCheck) ->
            nota to conteudo.findViewById<CheckBox>(idCheck).also { it.isChecked = nota in notasFiltradas }
        }

        val largura = dp(200)
        val popup = PopupWindow(conteudo, largura, ViewGroup.LayoutParams.WRAP_CONTENT, true).apply {
            elevation = dp(8).toFloat()
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        popupFiltros = popup

        conteudo.findViewById<View>(R.id.btnAplicarFiltros).setOnClickListener {
            notasFiltradas = checks.filter { it.second.isChecked }.map { it.first }.toSet()
            popup.dismiss()
            aoAplicar()
        }

        conteudo.findViewById<View>(R.id.btnLimparFiltros).setOnClickListener {
            checks.forEach { it.second.isChecked = false }
        }

        popup.setOnDismissListener {
            removerBlur()
            popupFiltros = null
        }

        aplicarBlur()

        // Painel alinhado à direita do botão "Filtros", logo abaixo dele
        popup.showAsDropDown(ancora, ancora.width - largura, dp(4))
    }

    private fun aplicarBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telaPerfil?.setRenderEffect(
                RenderEffect.createBlurEffect(7f, 7f, Shader.TileMode.CLAMP)
            )
        }
    }

    private fun removerBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telaPerfil?.setRenderEffect(null)
        }
    }

    private fun dp(valor: Int): Int = (valor * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        popupFiltros?.dismiss()
        removerBlur()
        popupFiltros = null
        telaPerfil = null
        super.onDestroyView()
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
