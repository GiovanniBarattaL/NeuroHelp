package com.example.neurohelp.Perfil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.neurohelp.ajuda.FragmentAjuda
import com.example.neurohelp.Login.Login
import com.example.neurohelp.R

class FragmentPerfil : Fragment() {

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
