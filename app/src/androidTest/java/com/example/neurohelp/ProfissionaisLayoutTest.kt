package com.example.neurohelp

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.test.platform.app.InstrumentationRegistry
import com.example.neurohelp.Profissionais.EstrelasHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfissionaisLayoutTest {
    @Test fun realProfessionalScreenContainsTheStarContainer() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.Theme_NeuroHelp)
            // Este é o layout inflado por FragmentProfissionais, não o card separado.
            val root = LayoutInflater.from(context).inflate(R.layout.profissionais_fragment, null)
            val container = root.findViewById<LinearLayout>(R.id.estrelasLista)
            assertNotNull("O layout da lista precisa conter estrelasLista", container)
            EstrelasHelper.preencher(container, 3.5, tamanhoDp = 14)
            assertEquals(5, container.childCount)
            for (index in 0 until container.childCount) {
                assertTrue(container.getChildAt(index) is ImageView)
                assertNotNull((container.getChildAt(index) as ImageView).drawable)
            }
        }
    }
}
