package com.example.neurohelp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class FragmentPerfil : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragmentperfil,
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

        }
}
