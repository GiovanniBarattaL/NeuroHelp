package com.example.neurohelp.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.neurohelp.Perfil.FragmentPerfil
import com.example.neurohelp.Profissionais.FragmentProfissionais
import com.example.neurohelp.R

class FragmentHome : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.home_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val imgPerfil =
            view.findViewById<ImageView>(R.id.imgPerfil)

        imgPerfil.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    FragmentPerfil()
                )
                .addToBackStack(null)
                .commit()
        }


        val btnProfissionais =
            view.findViewById<ImageView>(R.id.btnProfissionais)

        btnProfissionais.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    FragmentProfissionais()
                )
                .addToBackStack(null)
                .commit()
        }
    }
}