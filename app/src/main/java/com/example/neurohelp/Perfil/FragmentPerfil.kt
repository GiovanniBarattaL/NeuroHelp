package com.example.neurohelp.Perfil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.neurohelp.ajuda.FragmentAjuda
import com.example.neurohelp.Login.Login
import com.example.neurohelp.PrincipalActivity
import com.example.neurohelp.R
import com.example.neurohelp.configurarCabecalhoPadrao
import com.example.neurohelp.notificacoes.FragmentNotificacoes

class FragmentPerfil : Fragment() {
    private var enviandoFoto = false
    private val photoPicker = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        val raiz = view
        if (uri != null && raiz != null && !enviandoFoto) viewLifecycleOwner.lifecycleScope.launch {
            enviandoFoto = true
            val avatares = listOf(raiz.findViewById<android.widget.ImageView>(R.id.fotoConta),
                raiz.findViewById<android.widget.ImageView>(R.id.imgPerfil))
            avatares.forEach { it.isEnabled = false; it.alpha = 0.5f }
            try {
                com.example.neurohelp.auth.ProfilePhoto.upload(this@FragmentPerfil, uri)
                android.widget.Toast.makeText(requireContext(), "Foto de perfil salva na sua conta.", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (e: Exception) {
                if (isAdded && view === raiz && viewLifecycleOwner.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) {
                    com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Não foi possível salvar a foto")
                        .setMessage(com.example.neurohelp.auth.photoFailureMessage(e))
                        .setPositiveButton("Entendi", null)
                        .show()
                }
            } finally {
                enviandoFoto = false
                avatares.forEach { it.isEnabled = true; it.alpha = 1f }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.perfil_fragment,
            container,
            false
        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val avatar = view.findViewById<android.widget.ImageView>(R.id.fotoConta)
        com.example.neurohelp.auth.ProfilePhoto.own(this, avatar)
        enviandoFoto = false
        avatar.setOnClickListener { if (!enviandoFoto) photoPicker.launch("image/*") }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val user = org.json.JSONObject(com.example.neurohelp.auth.ApiService(com.example.neurohelp.auth.SessionStore(requireContext())).protectedRequest("/api/auth/me"))
                view.findViewById<android.widget.TextView>(R.id.txtNomeUsuario).text = user.optString("nome")
                view.findViewById<android.widget.TextView>(R.id.txtEmailUsuario).text = user.optString("email")
                // A autorização do envio é validada pela API da conta autenticada.
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) { /* Uma falha ao carregar nome/e-mail não bloqueia a seleção da foto. */ }
        }

        configurarCabecalhoPadrao(view, abrirPerfil = false) {
            (activity as? PrincipalActivity)?.irParaAba(R.id.nav_inicio)
        }

        view.findViewById<android.widget.ImageView>(R.id.imgPerfil).apply {
            contentDescription = "Alterar sua foto de perfil"
            isFocusable = true
            setOnClickListener { if (!enviandoFoto) photoPicker.launch("image/*") }
        }

        view.findViewById<View>(R.id.btnAlterarSenha).setOnClickListener {
            startActivity(Intent(requireContext(), com.example.neurohelp.Login.RecuperarSenha::class.java)
                .putExtra("perfil", true))
        }

        // Notificações
        view.findViewById<LinearLayout>(R.id.itemNotificacoes).setOnClickListener {
            abrirFragment(
                if (com.example.neurohelp.auth.SessionStore(requireContext()).papel() ==
                    com.example.neurohelp.auth.PapelUsuario.PROFISSIONAL
                ) com.example.neurohelp.notificacoes.FragmentNotificacoesProfissional()
                else FragmentNotificacoes()
            )
        }

        view.findViewById<View>(R.id.itemFavoritos).setOnClickListener {
            abrirFragment(com.example.neurohelp.Profissionais.FragmentProfissionais.favoritos())
        }

        // Privacidade e Segurança
        view.findViewById<LinearLayout>(R.id.itemPrivacidade).setOnClickListener {
            abrirFragment(
                if (com.example.neurohelp.auth.SessionStore(requireContext()).papel() ==
                    com.example.neurohelp.auth.PapelUsuario.PROFISSIONAL
                ) FragmentPrivacidadeProfissional()
                else FragmentPrivacidade()
            )
        }

        // Ajuda e suporte
        view.findViewById<LinearLayout>(R.id.itemAjuda).setOnClickListener {
            abrirFragment(FragmentAjuda())
        }

        // Sobre o EspectroCare
        view.findViewById<LinearLayout>(R.id.itemSobre).setOnClickListener {
            abrirFragment(FragmentSobre())
        }

        // Sair da conta: volta para o Login e limpa as telas anteriores
        view.findViewById<View>(R.id.btnSairConta).setOnClickListener {
            com.example.neurohelp.auth.SessionStore(requireContext()).clear()
            val intent = Intent(requireContext(), Login::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    private fun abrirFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
