package com.example.neurohelp.notificacoes

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.neurohelp.R

/** Lista de notificações do profissional (Figma: avatar colorido, "Pct. nome", mensagem e dia/hora). */
class NotificacoesProfissionalAdapter(
    private val aoClicar: (NotificacaoProfissional) -> Unit
) : ListAdapter<NotificacaoProfissional, NotificacoesProfissionalAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notificacoes_item_profissional, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.vincular(getItem(position), aoClicar)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val avatar: ImageView = itemView.findViewById(R.id.imgAvatarNotificacaoProf)
        private val nome: TextView = itemView.findViewById(R.id.txtNomeNotificacaoProf)
        private val mensagem: TextView = itemView.findViewById(R.id.txtMensagemNotificacaoProf)
        private val dia: TextView = itemView.findViewById(R.id.txtDiaNotificacaoProf)
        private val hora: TextView = itemView.findViewById(R.id.txtHoraNotificacaoProf)

        fun vincular(item: NotificacaoProfissional, aoClicar: (NotificacaoProfissional) -> Unit) {
            val contexto = itemView.context
            nome.text = contexto.getString(R.string.notificacao_prof_paciente, item.paciente)
            mensagem.text = item.tipo.mensagem
            dia.text = item.criadaEm.rotuloDia()
            hora.text = item.criadaEm.rotuloHora()

            avatar.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(contexto, item.tipo.corRes))

            itemView.setOnClickListener { aoClicar(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<NotificacaoProfissional>() {
        override fun areItemsTheSame(a: NotificacaoProfissional, b: NotificacaoProfissional) = a.id == b.id
        override fun areContentsTheSame(a: NotificacaoProfissional, b: NotificacaoProfissional) = a == b
    }
}
