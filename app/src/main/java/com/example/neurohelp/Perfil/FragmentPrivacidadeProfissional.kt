package com.example.neurohelp.Perfil

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.neurohelp.configurarDataNascimento
import com.example.neurohelp.R
import com.example.neurohelp.auth.ApiService
import com.example.neurohelp.auth.SessionStore
import com.example.neurohelp.configurarCabecalhoPadrao
import com.example.neurohelp.limitarLargura
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Privacidade e Segurança de quem entrou como PROFISSIONAL (Figma "Group 235").
 * A versão do responsável é a [FragmentPrivacidade].
 *
 * Cada seção tem o seu botão: "Editar" libera os campos e vira "Salvar"; "Salvar" trava de novo.
 * Os dados vêm de GET /api/auth/me (nome, e-mail e, se a API informar, telefone/CPF/endereço);
 * o que a API ainda não devolve fica como dica (hint) em cinza.
 *
 * TODO: enviar as alterações para a API quando existir o endpoint de atualização de perfil
 * (hoje "Salvar" só trava os campos, igual à tela do responsável).
 */
class FragmentPrivacidadeProfissional : Fragment() {

    /** E-mail real; o campo mostra a versão mascarada enquanto está travado. */
    private var emailReal: String = ""

    private var editandoDocumentos = false

    private val seletorDocumento =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            val raiz = view ?: return@registerForActivityResult
            if (uri == null) return@registerForActivityResult
            // TODO: enviar o arquivo para a API (hoje só mostramos o nome escolhido)
            raiz.findViewById<TextView>(R.id.txtArquivoDocumento).text = nomeDoArquivo(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.perfil_privacidade_profissional, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarCabecalhoPadrao(view)
        configurarDataNascimento(view, R.id.etProfDataNasc)
        view.findViewById<EditText>(R.id.etProfSenha).isSaveEnabled = false

        // Tablet/paisagem: formulário centralizado com largura máxima
        val maxPx = (LARGURA_MAX_DP * resources.displayMetrics.density).toInt()
        view.findViewById<View>(R.id.conteudoPrivacidadeProf).limitarLargura(maxPx)

        // ---------- Informações pessoais ----------
        configurarEdicao(
            botao = view.findViewById(R.id.btnEditarProfPessoais),
            campos = ids(view, R.id.etProfNome, R.id.etProfTelefone, R.id.etProfCpf, R.id.etProfDataNasc)
        )

        // ---------- Endereço ----------
        configurarEdicao(
            botao = view.findViewById(R.id.btnEditarProfEndereco),
            campos = ids(
                view, R.id.etProfCep, R.id.etProfUf, R.id.etProfCidade,
                R.id.etProfBairro, R.id.etProfLogradouro, R.id.etProfNumero
            )
        )

        // ---------- Formação profissional ----------
        configurarEdicao(
            botao = view.findViewById(R.id.btnEditarProfFormacao),
            campos = ids(view, R.id.etProfFormacao)
        )

        // ---------- Documentos ----------
        configurarDocumentos(view)

        // ---------- Segurança ----------
        val etEmail = view.findViewById<EditText>(R.id.etProfEmail)
        val etSenha = view.findViewById<EditText>(R.id.etProfSenha)
        configurarEdicao(
            botao = view.findViewById(R.id.btnEditarProfSeguranca),
            campos = listOf(etEmail, etSenha),
            aoEditar = {
                // Mostra o e-mail de verdade para poder alterar
                etEmail.setText(emailReal)
                etEmail.setSelection(etEmail.text.length)
            },
            aoSalvar = {
                emailReal = etEmail.text.toString().trim()
                etEmail.setText(mascararEmail(emailReal))
                etSenha.setText("") // nunca guarda a senha digitada na tela
            }
        )

        carregarDadosDaConta(view)
    }

    /** Preenche o que a API já informa (nome, e-mail e campos opcionais). */
    private fun carregarDadosDaConta(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val api = ApiService(SessionStore(requireContext()))
                val conta = JSONObject(api.protectedRequest("/api/auth/me"))

                preencher(view, R.id.etProfNome, conta.optString("nome"))
                preencher(view, R.id.etProfTelefone, conta.optString("telefone").filter { it in '0'..'9' })
                preencher(view, R.id.etProfCpf, conta.optString("cpf").filter { it in '0'..'9' })
                preencher(view, R.id.etProfDataNasc, conta.optString("dataNascimento"))
                preencher(view, R.id.etProfCep, conta.optString("cep").filter { it in '0'..'9' })
                preencher(view, R.id.etProfUf, conta.optString("estado"))
                preencher(view, R.id.etProfCidade, conta.optString("cidade"))
                preencher(view, R.id.etProfBairro, conta.optString("bairro"))
                preencher(view, R.id.etProfLogradouro, conta.optString("logradouro"))
                preencher(view, R.id.etProfNumero, conta.optString("numero"))

                val formacao = listOf(conta.optString("profissao"), conta.optString("numRegistro"))
                    .filter { it.isNotBlank() }
                    .joinToString(" · ")
                preencher(view, R.id.etProfFormacao, formacao)

                emailReal = conta.optString("email")
                preencher(view, R.id.etProfEmail, mascararEmail(emailReal))
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Sem conexão/sessão expirada: a tela continua mostrando as dicas em cinza
            }
        }
    }

    private fun preencher(view: View, id: Int, texto: String) {
        // optString devolve "null" como texto quando o campo é nulo no JSON
        if (texto.isBlank() || texto == "null") return
        val campo = view.findViewById<EditText>(id)
        if (!campo.isEnabled) campo.setText(texto)
    }

    private fun ids(view: View, vararg ids: Int): List<View> = ids.map { view.findViewById<View>(it) }

    /**
     * Botão "Editar": libera os campos da seção e vira "Salvar".
     * Ao tocar em "Salvar", trava os campos de novo.
     */
    private fun configurarEdicao(
        botao: TextView,
        campos: List<View>,
        aoEditar: () -> Unit = {},
        aoSalvar: () -> Unit = {}
    ) {
        var editando = false

        botao.setOnClickListener {
            if (editando) {
                android.widget.Toast.makeText(requireContext(), R.string.recurso_nao_disponivel, android.widget.Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            editando = !editando

            campos.forEach { it.isEnabled = editando }

            if (editando) {
                botao.text = getString(R.string.privacidade_salvar)
                aoEditar()
                campos.firstOrNull { it is EditText }?.requestFocus()
            } else {
                botao.text = getString(R.string.privacidade_editar)
                campos.forEach { it.clearFocus() }
                esconderTeclado(botao)
                aoSalvar()
                // TODO: enviar os dados atualizados para a API
            }
        }
    }

    /** Documentos: "Editar" libera o toque na caixa de envio (PDF ou imagem). */
    private fun configurarDocumentos(view: View) {
        val area = view.findViewById<View>(R.id.areaDocumentos)
        val botao = view.findViewById<TextView>(R.id.btnEditarProfDocumentos)
        area.isEnabled = false
        area.alpha = 0.6f

        area.setOnClickListener {
            if (editandoDocumentos) seletorDocumento.launch(arrayOf("application/pdf", "image/*"))
        }

        botao.setOnClickListener {
            if (editandoDocumentos) {
                Toast.makeText(requireContext(), R.string.recurso_nao_disponivel, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            editandoDocumentos = !editandoDocumentos
            area.isEnabled = editandoDocumentos
            area.alpha = if (editandoDocumentos) 1f else 0.6f
            botao.text = getString(
                if (editandoDocumentos) R.string.privacidade_salvar else R.string.privacidade_editar
            )
            if (!editandoDocumentos) {
                Toast.makeText(requireContext(), "Documento selecionado.", Toast.LENGTH_SHORT).show()
                // TODO: enviar o documento para a API
            }
        }
    }

    private fun nomeDoArquivo(uri: Uri): String {
        return try {
            requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val coluna = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (coluna >= 0 && cursor.moveToFirst()) return cursor.getString(coluna)
            }
            "Arquivo selecionado"
        } catch (_: SecurityException) {
            "Arquivo indisponível"
        } catch (_: IllegalArgumentException) {
            "Arquivo indisponível"
        }
    }

    private fun esconderTeclado(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private companion object {
        const val LARGURA_MAX_DP = 560

        /** joao.silva@gmail.com -> joa******@gmail.com */
        fun mascararEmail(email: String): String {
            val arroba = email.indexOf('@')
            if (arroba <= 0) return email
            val usuario = email.substring(0, arroba)
            val visivel = usuario.take(3.coerceAtMost(usuario.length))
            return visivel + "******" + email.substring(arroba)
        }
    }
}
