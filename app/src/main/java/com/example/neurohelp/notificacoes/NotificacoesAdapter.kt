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

class NotificacoesAdapter(
    private val aoClicar: (Notificacao) -> Unit
) : ListAdapter<Notificacao, NotificacoesAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notificacoes_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.vincular(getItem(position), aoClicar)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val avatar: ImageView = itemView.findViewById(R.id.imgAvatarNotificacao)
        private val nome: TextView = itemView.findViewById(R.id.txtNomeNotificacao)
        private val mensagem: TextView = itemView.findViewById(R.id.txtMensagemNotificacao)

        fun vincular(notificacao: Notificacao, aoClicar: (Notificacao) -> Unit) {
            nome.text = notificacao.consulta.profissional
            mensagem.text = notificacao.mensagem

            // Roxo = ainda não vista; verde depois de aberta (Figma)
            val cor = if (notificacao.visto) {
                ContextCompat.getColor(itemView.context, R.color.agenda_verde_escuro)
            } else {
                ContextCompat.getColor(itemView.context, R.color.notificacao_nao_vista)
            }
            avatar.backgroundTintList = ColorStateList.valueOf(cor)

            itemView.setOnClickListener { aoClicar(notificacao) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Notificacao>() {
        override fun areItemsTheSame(a: Notificacao, b: Notificacao) = a.id == b.id
        override fun areContentsTheSame(a: Notificacao, b: Notificacao) = a == b
    }
}
