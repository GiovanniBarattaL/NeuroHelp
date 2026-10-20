package com.example.neurohelp.notificacoes

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.neurohelp.R

/**
 * Pop-up de detalhe da notificação do profissional (Figma): card centralizado sobre fundo escurecido
 * com o paciente, o que ele fez, o texto que escreveu e o dia/hora. Toque fora ou voltar fecha.
 */
class DetalheNotificacaoProfissionalDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setDimAmount(0.45f)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.notificacoes_dialog_profissional, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = arguments?.getString(ARG_ID)?.let { NotificacoesProfissionalStore.buscar(it) }
        if (item == null) {
            dismissAllowingStateLoss()
            return
        }

        view.findViewById<TextView>(R.id.txtDialogPaciente).text =
            getString(R.string.notificacao_prof_paciente, item.paciente)
        view.findViewById<TextView>(R.id.txtDialogMensagem).text = item.tipo.mensagemDetalhe
        view.findViewById<TextView>(R.id.txtDialogDia).text = item.criadaEm.rotuloDia()
        view.findViewById<TextView>(R.id.txtDialogHora).text = item.criadaEm.rotuloHora()

        // Sem texto do paciente (ex.: só confirmou), o bloco some em vez de ficar vazio
        view.findViewById<View>(R.id.scrollDialogDetalhe).visibility =
            if (item.observacao.isNullOrBlank()) View.GONE else View.VISIBLE
        view.findViewById<TextView>(R.id.txtDialogDetalhe).text = item.observacao.orEmpty()
    }

    override fun onStart() {
        super.onStart()
        // 90% da largura da tela, no máximo 420dp (tablets não esticam)
        val densidade = resources.displayMetrics.density
        val largura = minOf((resources.displayMetrics.widthPixels * 0.9f).toInt(), (420 * densidade).toInt())
        dialog?.window?.setLayout(largura, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        const val TAG = "DetalheNotificacaoProfissional"
        private const val ARG_ID = "id"

        fun newInstance(id: String) = DetalheNotificacaoProfissionalDialogFragment().apply {
            arguments = Bundle().apply { putString(ARG_ID, id) }
        }
    }
}
