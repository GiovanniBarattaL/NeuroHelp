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
    private val photoPicker = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && view != null) viewLifecycleOwner.lifecycleScope.launch {
            val avatar = requireView().findViewById<android.widget.ImageView>(R.id.fotoConta)
            avatar.isEnabled = false
            try {
                val bitmap = com.example.neurohelp.auth.ProfilePhoto.upload(this@FragmentPerfil, uri)
                avatar.imageTintList = null; avatar.setImageBitmap(bitmap)
                android.widget.Toast.makeText(requireContext(), "Foto atualizada.", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) { android.widget.Toast.makeText(requireContext(), "Não foi possível salvar. Escolha JPG, PNG ou WebP de até 5 MB e verifique sua conexão.", android.widget.Toast.LENGTH_LONG).show() }
            finally { avatar.isEnabled = true }
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
        avatar.setOnClickListener { photoPicker.launch("image/*") }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val user = org.json.JSONObject(com.example.neurohelp.auth.ApiService(com.example.neurohelp.auth.SessionStore(requireContext())).protectedRequest("/api/auth/me"))
                view.findViewById<android.widget.TextView>(R.id.txtNomeUsuario).text = user.optString("nome")
                view.findViewById<android.widget.TextView>(R.id.txtEmailUsuario).text = user.optString("email")
                avatar.isEnabled = user.optString("tipoPerfil") in setOf("RESPONSAVEL", "PROFISSIONAL")
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) { avatar.isEnabled = false }
        }

        configurarCabecalhoPadrao(view, abrirPerfil = false) {
            (activity as? PrincipalActivity)?.irParaAba(R.id.nav_inicio)
        }

        // Notificações
        view.findViewById<LinearLayout>(R.id.itemNotificacoes).setOnClickListener {
            abrirFragment(FragmentNotificacoes())
        }

        // Privacidade e Segurança
        view.findViewById<LinearLayout>(R.id.itemPrivacidade).setOnClickListener {
            abrirFragment(FragmentPrivacidade())
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
