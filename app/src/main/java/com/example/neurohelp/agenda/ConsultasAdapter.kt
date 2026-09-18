package com.example.neurohelp.agenda

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
import com.google.android.material.button.MaterialButton

class ConsultasAdapter(
    private val aoClicarDetalhes: (Consulta) -> Unit
) : ListAdapter<ItemAgenda, RecyclerView.ViewHolder>(Diff) {

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is ItemAgenda.Secao -> TIPO_SECAO
        is ItemAgenda.Item -> TIPO_CONSULTA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TIPO_SECAO) {
            SecaoViewHolder(inflater.inflate(R.layout.item_secao_agenda, parent, false))
        } else {
            ConsultaViewHolder(inflater.inflate(R.layout.item_consulta, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ItemAgenda.Secao -> (holder as SecaoViewHolder).vincular(item)
            is ItemAgenda.Item -> (holder as ConsultaViewHolder).vincular(item.consulta, aoClicarDetalhes)
        }
    }

    class SecaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtSecao: TextView = itemView.findViewById(R.id.txtSecao)

        fun vincular(secao: ItemAgenda.Secao) {
            txtSecao.text = secao.titulo
        }
    }

    class ConsultaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewBarraCor: View = itemView.findViewById(R.id.viewBarraCor)
        private val imgIcone: ImageView = itemView.findViewById(R.id.imgIconeEspecialidade)
        private val txtTitulo: TextView = itemView.findViewById(R.id.txtTituloConsulta)
        private val txtProfissional: TextView = itemView.findViewById(R.id.txtProfissional)
        private val txtHorario: TextView = itemView.findViewById(R.id.txtHorario)
        private val imgModalidade: ImageView = itemView.findViewById(R.id.imgModalidade)
        private val txtModalidade: TextView = itemView.findViewById(R.id.txtModalidade)
        private val btnDetalhes: MaterialButton = itemView.findViewById(R.id.btnDetalhes)

        fun vincular(consulta: Consulta, aoClicarDetalhes: (Consulta) -> Unit) {
            val contexto = itemView.context
            val cor = ContextCompat.getColor(contexto, consulta.especialidade.corRes)
            val listaCor = ColorStateList.valueOf(cor)

            viewBarraCor.setBackgroundColor(cor)
            imgIcone.backgroundTintList = listaCor

            txtTitulo.text = contexto.getString(
                R.string.agenda_titulo_consulta,
                contexto.getString(consulta.especialidade.nomeRes)
            )
            txtProfissional.text = consulta.profissional
            txtHorario.text = consulta.inicio.horaFormatada()
            txtModalidade.text = consulta.local
                ?: contexto.getString(consulta.modalidade.nomeRes)
            imgModalidade.setImageResource(
                when (consulta.modalidade) {
                    ModalidadeConsulta.ONLINE -> R.drawable.ic_online_agenda
                    ModalidadeConsulta.PRESENCIAL -> R.drawable.ic_local_agenda
                }
            )

            btnDetalhes.strokeColor = listaCor
            btnDetalhes.setTextColor(cor)
            btnDetalhes.setOnClickListener { aoClicarDetalhes(consulta) }
            itemView.setOnClickListener { aoClicarDetalhes(consulta) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<ItemAgenda>() {
        override fun areItemsTheSame(oldItem: ItemAgenda, newItem: ItemAgenda) = when {
            oldItem is ItemAgenda.Secao && newItem is ItemAgenda.Secao ->
                oldItem.titulo == newItem.titulo

            oldItem is ItemAgenda.Item && newItem is ItemAgenda.Item ->
                oldItem.consulta.id == newItem.consulta.id

            else -> false
        }

        override fun areContentsTheSame(oldItem: ItemAgenda, newItem: ItemAgenda) = oldItem == newItem
    }

    private companion object {
        const val TIPO_SECAO = 0
        const val TIPO_CONSULTA = 1
    }
}
