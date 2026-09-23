package com.example.neurohelp.Perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.neurohelp.FragmentAjuda
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

        val btnsobre = view.findViewById<LinearLayout>(R.id.itemSobre)
        btnsobre.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, FragmentSobre())
                    .addToBackStack(null)
                    .commit()
            }
        val btnajuda = view.findViewById<LinearLayout>(R.id.itemAjuda)
        btnajuda.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentAjuda())
                .addToBackStack(null)
                .commit()
        }

        }
}