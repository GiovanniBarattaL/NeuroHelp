package com.example.neurohelp

import android.app.DatePickerDialog
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import java.util.Calendar
import java.util.Locale

/** Nascimento só pelo calendário: impede dias inexistentes e datas futuras. */
fun Fragment.configurarDataNascimento(raiz: View, id: Int) {
    val campo = raiz.findViewById<EditText>(id)
    campo.isFocusable = false
    campo.isClickable = true
    campo.showSoftInputOnFocus = false
    campo.setOnClickListener {
        if (!campo.isEnabled) return@setOnClickListener
        val hoje = Calendar.getInstance()
        val inicial = Calendar.getInstance()
        val partes = campo.text.toString().split("/")
        if (partes.size == 3) {
            val dia = partes[0].toIntOrNull()
            val mes = partes[1].toIntOrNull()
            val ano = partes[2].toIntOrNull()
            if (dia != null && mes != null && ano != null) {
                try {
                    inicial.isLenient = false
                    inicial.set(ano, mes - 1, dia)
                    if (inicial.timeInMillis > hoje.timeInMillis) inicial.timeInMillis = hoje.timeInMillis
                } catch (_: IllegalArgumentException) {
                    inicial.timeInMillis = hoje.timeInMillis
                }
            }
        }
        DatePickerDialog(requireContext(), { _, ano, mes, dia ->
            campo.setText(String.format(Locale.ROOT, "%02d/%02d/%04d", dia, mes + 1, ano))
        }, inicial.get(Calendar.YEAR), inicial.get(Calendar.MONTH), inicial.get(Calendar.DAY_OF_MONTH))
            .apply { datePicker.maxDate = hoje.timeInMillis }
            .show()
    }
}

/** Remove pontuação antes do LengthFilter, inclusive ao colar CPF/telefone/CEP. */
fun View.configurarCamposNumericos() {
    val ids = listOf(R.id.etCpf, R.id.etRespCpf, R.id.etDepCpf, R.id.etProfCpf,
        R.id.etTelefone, R.id.etRespTelefone, R.id.etProfTelefone, R.id.etCep, R.id.etProfCep)
    ids.forEach { id ->
        val campo = findViewById<EditText>(id) ?: return@forEach
        val normalizador = android.text.InputFilter { source, start, end, _, _, _ ->
            val entrada = source.subSequence(start, end)
            var numeros = entrada.filter { it in '0'..'9' }.toString()
            if (id == R.id.etTelefone || id == R.id.etRespTelefone || id == R.id.etProfTelefone) {
                if (numeros.length in 12..13 && numeros.startsWith("55")) numeros = numeros.drop(2)
            }
            if (numeros == entrada.toString()) null else numeros
        }
        campo.filters = arrayOf(normalizador, *campo.filters)
    }
}
