package com.example.neurohelp

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** DialogFragment mantém o aviso ao girar a tela e evita janelas duplicadas. */
class ConfirmarSaidaDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.sair_app_titulo)
            .setMessage(R.string.sair_app_pergunta)
            .setNegativeButton(R.string.sair_app_cancelar, null)
            .setPositiveButton(R.string.sair_app_confirmar) { _, _ -> requireActivity().finish() }
            .create()

    companion object { const val TAG = "confirmar_saida_app" }
}
