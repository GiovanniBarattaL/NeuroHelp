package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class FragmentHome : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_home,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnperfil = view.findViewById<ImageView>(R.id.imgPerfil)
        btnperfil.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentPerfil())
                .addToBackStack(null)
                .commit()
        }
        val btnprof = view.findViewById<ImageView>(R.id.profissionaisbtnaba)
        btnprof.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentProfissionais())
                .addToBackStack(null)
                .commit()
        }

    }
}