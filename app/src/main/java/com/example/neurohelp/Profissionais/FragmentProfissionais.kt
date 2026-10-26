package com.example.neurohelp.Profissionais

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.neurohelp.PrincipalActivity
import com.example.neurohelp.R
import com.example.neurohelp.auth.ehProfissional
import com.example.neurohelp.configurarCabecalhoPadrao
import java.text.Normalizer

/** Busca e favoritos sobre o único registro de demonstração existente no projeto. */
class FragmentProfissionais : Fragment() {
    private var dialogFiltros: Dialog? = null
    private var especialidades = setOf<Int>()
    private var modalidades = setOf<Int>()
    private var notas = setOf<Int>()
    private var cidade = 0
    private val profissional = ProfissionalMock.exemplo(1)
    val somenteFavoritos: Boolean get() = arguments?.getBoolean("somente_favoritos") == true

    companion object {
        fun favoritos() = FragmentProfissionais().apply {
            arguments = bundleOf("somente_favoritos" to true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.profissionais_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        especialidades = savedInstanceState?.getIntArray("especialidades")?.toSet().orEmpty()
        modalidades = savedInstanceState?.getIntArray("modalidades")?.toSet().orEmpty()
        notas = savedInstanceState?.getIntArray("notas")?.toSet().orEmpty()
        cidade = savedInstanceState?.getInt("cidade") ?: 0
        configurarCabecalhoPadrao(view) {
            if (somenteFavoritos) parentFragmentManager.popBackStack()
            else (activity as? PrincipalActivity)?.irParaAba(R.id.nav_inicio)
        }
        view.findViewById<EditText>(R.id.edtBusca).doAfterTextChanged { atualizarLista() }
        view.findViewById<View>(R.id.btnFiltros).setOnClickListener { abrirFiltros() }
        view.findViewById<View>(R.id.btnPerfil1).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentPerfilProfissional.newInstance(profissional.id))
                .addToBackStack(null).commit()
        }
        view.findViewById<View>(R.id.btnAgendar1).apply {
            isEnabled = !ehProfissional()
            if (!isEnabled) setBackgroundResource(R.drawable.botao_desabilitado)
            else setOnClickListener { abrirWhatsAppProfissional(profissional.nome, profissional.whatsapp) }
        }
        view.findViewById<TextView>(R.id.txtNome1).text = profissional.nome
        view.findViewById<TextView>(R.id.txtProfissao1).text = profissional.profissao
        view.findViewById<TextView>(R.id.txtcrp).text = profissional.registro
        view.findViewById<TextView>(R.id.txtOnline1).text = profissional.atendimento
        view.findViewById<TextView>(R.id.txtLocal1).text = profissional.local
        EstrelasHelper.preencher(view.findViewById(R.id.estrelasLista), profissional.notaMedia, tamanhoDp = 14)
        view.findViewById<View>(R.id.layoutFavorito1).apply {
            isClickable = true
            isFocusable = true
            minimumHeight = dp(48)
            setOnClickListener {
                val store = FavoritosStore(requireContext())
                store.definir(profissional.id, !store.contem(profissional.id))
                atualizarLista()
            }
        }
        atualizarLista()
    }

    override fun onResume() {
        super.onResume()
        atualizarLista() // reflete o coração alterado na tela de perfil
    }

    private fun atualizarLista() {
        val raiz = view ?: return
        fun normalizar(s: String) = Normalizer.normalize(s, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "").lowercase(java.util.Locale.ROOT)
        val busca = normalizar(raiz.findViewById<EditText>(R.id.edtBusca).text.toString().trim())
        val favorito = FavoritosStore(requireContext()).contem(profissional.id)
        val texto = normalizar("${profissional.nome} ${profissional.profissao} ${profissional.registro} ${profissional.local}")
        // O registro de demonstração é psicóloga, atende online e presencial, em São Paulo.
        val mostrar = texto.contains(busca) && (!somenteFavoritos || favorito) &&
            (especialidades.isEmpty() || 0 in especialidades) &&
            (modalidades.isEmpty() || modalidades.any { it in 0..2 }) &&
            (notas.isEmpty() || profissional.notaMedia.toInt() in notas) && cidade in 0..1
        raiz.findViewById<View>(R.id.cardProfissional1).visibility = if (mostrar) View.VISIBLE else View.GONE
        raiz.findViewById<TextView>(R.id.txtQuantidade).text = when {
            !mostrar -> "Nenhum profissional encontrado"
            somenteFavoritos -> "1 profissional favorito"
            else -> "1 profissional encontrado"
        }
        raiz.findViewById<TextView>(R.id.txtFavorito1).text = if (favorito) "Favoritado" else "Favoritar"
        raiz.findViewById<android.widget.ImageView>(R.id.imgFavorito1).setImageResource(
            if (favorito) R.drawable.ic_favorito_cheio else R.drawable.ic_favorito
        )
    }

    private fun abrirFiltros() {
        if (dialogFiltros?.isShowing == true) return
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.profissionais_filtros)
        dialogFiltros = dialog
        val idsEspecialidades = listOf(R.id.checkEspecialidade1, R.id.checkEspecialidade2,
            R.id.checkEspecialidade3, R.id.checkEspecialidade4, R.id.checkEspecialidade5)
        val nomes = listOf("Psicologia", "Fonoaudiologia", "Terapia ocupacional", "Neuropediatria", "Nutrição")
        val checksEspecialidades = idsEspecialidades.mapIndexed { i, id ->
            dialog.findViewById<CheckBox>(id).apply { text = nomes[i]; isChecked = i in especialidades }
        }
        val checksModalidades = listOf(R.id.checkOnline, R.id.checkPresencial, R.id.checkHibrido)
            .mapIndexed { i, id -> dialog.findViewById<CheckBox>(id).apply { isChecked = i in modalidades } }
        val checksNotas = listOf(5 to R.id.check5Estrelas, 4 to R.id.check4Estrelas,
            3 to R.id.check3Estrelas, 2 to R.id.check2Estrelas, 1 to R.id.check1Estrela)
            .map { (nota, id) -> nota to dialog.findViewById<CheckBox>(id).apply { isChecked = nota in notas } }
        val spinner = dialog.findViewById<Spinner>(R.id.spinnerCidade)
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            listOf("Todas as cidades", profissional.local))
        spinner.setSelection(cidade.coerceIn(0, 1))
        dialog.findViewById<View>(R.id.btnAplicarFiltros).setOnClickListener {
            especialidades = checksEspecialidades.indices.filter { checksEspecialidades[it].isChecked }.toSet()
            modalidades = checksModalidades.indices.filter { checksModalidades[it].isChecked }.toSet()
            notas = checksNotas.filter { it.second.isChecked }.map { it.first }.toSet()
            cidade = spinner.selectedItemPosition
            atualizarLista()
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.btnLimparFiltros).setOnClickListener {
            (checksEspecialidades + checksModalidades + checksNotas.map { it.second }).forEach { it.isChecked = false }
            spinner.setSelection(0)
        }
        dialog.setOnDismissListener { dialogFiltros = null }
        dialog.show()
        val quadro = android.graphics.Rect()
        requireActivity().window.decorView.getWindowVisibleDisplayFrame(quadro)
        val largura = minOf(dp(360), (quadro.width() - dp(32)).coerceAtLeast(1))
        val alturaMax = (quadro.height() * 0.85f).toInt().coerceAtLeast(1)
        val conteudo = dialog.findViewById<View>(android.R.id.content)
        conteudo.measure(View.MeasureSpec.makeMeasureSpec(largura, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(alturaMax, View.MeasureSpec.AT_MOST))
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setGravity(android.view.Gravity.CENTER)
            setLayout(largura, minOf(conteudo.measuredHeight, alturaMax))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntArray("especialidades", especialidades.toIntArray())
        outState.putIntArray("modalidades", modalidades.toIntArray())
        outState.putIntArray("notas", notas.toIntArray())
        outState.putInt("cidade", cidade)
        super.onSaveInstanceState(outState)
    }

    private fun dp(valor: Int) = (valor * resources.displayMetrics.density).toInt()
    override fun onDestroyView() {
        dialogFiltros?.dismiss()
        dialogFiltros = null
        super.onDestroyView()
    }
}
