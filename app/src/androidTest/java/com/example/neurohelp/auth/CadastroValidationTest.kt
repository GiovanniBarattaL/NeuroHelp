package com.example.neurohelp.auth

import android.widget.EditText
import android.widget.LinearLayout
import androidx.test.platform.app.InstrumentationRegistry
import com.example.neurohelp.R
import com.example.neurohelp.configurarCamposNumericos
import org.junit.Assert.*
import org.junit.Test

class CadastroValidationTest {
    @Test fun telefoneLimitaDigitacaoColagemESubstituicao() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val root = LinearLayout(instrumentation.targetContext)
            val phone = EditText(instrumentation.targetContext).apply { id = R.id.etTelefone }
            root.addView(phone)
            root.configurarCamposNumericos()
            phone.setText("(11) 99999-9999")
            assertEquals("11999999999", phone.text.toString())
            phone.append("9")
            assertEquals(11, phone.length())
            phone.text.replace(0, phone.length(), "119999999999999")
            assertEquals("11999999999", phone.text.toString())
        }
    }

    @Test fun erroDeValidacaoNaoMostraValoresOuDetalhesInternos() {
        assertEquals("Telefone: Telefone deve possuir 10 ou 11 dígitos.", ApiService.validationMessage(400,
            """{"campos":{"telefone":"Telefone deve possuir 10 ou 11 dígitos."}}"""))
        assertNull(ApiService.validationMessage(400, """{"campos":{"senha":"segredo"}}"""))
        assertNull(ApiService.validationMessage(500, "stack trace"))
        assertTrue(ApiService.validationMessage(422, "")!!.contains("código"))
    }
}
