package com.example.neurohelp.Perfil

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.neurohelp.configurarDataNascimento
import com.example.neurohelp.R
import com.example.neurohelp.configurarCabecalhoPadrao

class FragmentPrivacidade : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.perfil_privacidade,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cabeçalho padrão (voltar fecha esse fragment e volta pro anterior na back stack)
        configurarCabecalhoPadrao(view)
        configurarDataNascimento(view, R.id.etRespDataNasc)
        configurarDataNascimento(view, R.id.etDepDataNasc)
        view.findViewById<EditText>(R.id.etSenha).isSaveEnabled = false

        // ---------- Responsável ----------
        val camposResponsavel = listOf<View>(
            view.findViewById(R.id.etRespNome),
            view.findViewById(R.id.etRespSexo),
            view.findViewById(R.id.etRespTelefone),
            view.findViewById(R.id.etRespCpf),
            view.findViewById(R.id.etRespDataNasc),
            view.findViewById(R.id.etRespCidade),
            view.findViewById(R.id.etRespUf)
        )
        configurarEdicao(
            botao = view.findViewById(R.id.btnEditarResponsavel),
            campos = camposResponsavel
        )

        // ---------- Dependente ----------
        val checkSim = view.findViewById<CheckBox>(R.id.checkDiagSim)
        val checkAnalise = view.findViewById<CheckBox>(R.id.checkDiagAnalise)
        val checkNao = view.findViewById<CheckBox>(R.id.checkDiagNao)

        // Só uma opção de diagnóstico pode ficar marcada por vez
        val opcoesDiagnostico = listOf(checkSim, checkAnalise, checkNao)
        var atualizandoDiagnostico = false
        opcoesDiagnostico.forEach { escolhida ->
            escolhida.setOnCheckedChangeListener { _, marcado ->
                if (atualizandoDiagnostico) return@setOnCheckedChangeListener
                atualizandoDiagnostico = true
                if (marcado) {
                    opcoesDiagnostico.filter { it !== escolhida }.forEach { it.isChecked = false }
                } else if (opcoesDiagnostico.none { it.isChecked }) {
                    // não deixa ficar sem nenhuma marcada
                    escolhida.isChecked = true
                }
                atualizandoDiagnostico = false
            }
        }

        val camposDependente = listOf<View>(
            view.findViewById(R.id.etDepNome),
            view.findViewById(R.id.etDepCpf),
            view.findViewById(R.id.etDepSexo),
            view.findViewById(R.id.etDepDataNasc),
            checkSim,
            checkAnalise,
            checkNao
        )
        configurarEdicao(
            botao = view.findViewById(R.id.btnEditarDependente),
            campos = camposDependente
        )

        // ---------- Segurança ----------
        configurarEdicao(
            botao = view.findViewById(R.id.btnEditarSeguranca),
            campos = listOf(
                view.findViewById<View>(R.id.etEmail),
                view.findViewById<View>(R.id.etSenha)
            )
        )
    }

    /**
     * Botão "Editar": libera os campos da seção e vira "Salvar".
     * Ao tocar em "Salvar", trava os campos de novo.
     */
    private fun configurarEdicao(botao: TextView, campos: List<View>) {
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
                campos.firstOrNull { it is EditText }?.requestFocus()
            } else {
                botao.text = getString(R.string.privacidade_editar)
                campos.forEach { it.clearFocus() }
                esconderTeclado(botao)
                // TODO: enviar os dados atualizados para a API/Firestore/Room
            }
        }
    }

    private fun esconderTeclado(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
