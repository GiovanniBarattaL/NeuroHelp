package com.example.neurohelp

import android.app.Activity
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test

/** Exercita o layout real sem login nem chamada ao servidor. */
class KeyboardInsetsTest {
    @Test fun keyboardDoesNotCreateASecondBottomGap() {
        assumeTrue(android.os.Build.VERSION.SDK_INT >= 30)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.Theme_NeuroHelp)
            val root = LayoutInflater.from(context).inflate(R.layout.activity_principal, null) as ViewGroup
            val menu = root.findViewById<View>(R.id.bottomNavigation)
            val originalMenuPadding = menu.paddingBottom
            // A extensão só usa as views: não exige Activity anexada ou sessão.
            Activity().aplicarInsetsLateraisNaRaiz(root)

            fun dispatch(bottom: Int, keyboard: Int, left: Int = 0) {
                val insets = WindowInsetsCompat.Builder()
                    .setInsets(WindowInsetsCompat.Type.systemBars(), Insets.of(left, 32, 0, bottom))
                    .setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, keyboard))
                    .setVisible(WindowInsetsCompat.Type.ime(), keyboard > 0)
                    .build()
                ViewCompat.dispatchApplyWindowInsets(root, insets)
                root.measure(View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.EXACTLY))
                root.layout(0, 0, 1080, 2000)
            }

            for (navigationBar in listOf(24, 80)) {
                dispatch(navigationBar, 0)
                assertEquals(View.VISIBLE, menu.visibility)
                assertEquals(navigationBar, root.paddingBottom)
                val fullFormHeight = root.findViewById<View>(R.id.fragmentContainer).height

                dispatch(navigationBar, 700)
                assertEquals(View.GONE, menu.visibility)
                assertEquals(700, root.paddingBottom)
                assertEquals(1300, root.findViewById<View>(R.id.fragmentContainer).height)

                dispatch(navigationBar, 0)
                assertEquals(View.VISIBLE, menu.visibility)
                assertEquals(originalMenuPadding, menu.paddingBottom)
                assertEquals(fullFormHeight, root.findViewById<View>(R.id.fragmentContainer).height)
            }
            dispatch(24, 700, left = 48)
            assertEquals(48, root.paddingLeft)
            // Topo continua pertencendo ao cabeçalho, sem margem extra na raiz.
            assertEquals(0, root.paddingTop)
        }
    }
}
