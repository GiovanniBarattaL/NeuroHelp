package com.example.neurohelp.agenda

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.neurohelp.R

class DetalhesConsultaDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.agenda_dialog_consulta_detalhes)

        dialog.setCanceledOnTouchOutside(true)

        dialog.window?.apply {

            // Fundo transparente para aparecer somente o
            // bg_dialog_consulta do layout
            setBackgroundDrawableResource(
                android.R.color.transparent
            )

            // Escurece a agenda atrás do popup
            setDimAmount(0.60f)

            addFlags(
                android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
            )
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {

            // Largura do popup
            setLayout(
                (resources.displayMetrics.widthPixels * 0.88).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}