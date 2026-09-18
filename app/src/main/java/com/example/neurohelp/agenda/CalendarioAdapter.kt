package com.example.neurohelp.agenda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.neurohelp.R
import java.util.Date

class CalendarioAdapter(
    private val aoSelecionarDia: (Date) -> Unit
) : ListAdapter<DiaCalendario, CalendarioAdapter.DiaViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_calendario, parent, false)
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        holder.vincular(getItem(position), aoSelecionarDia)
    }

    class DiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txtDia: TextView = itemView.findViewById(R.id.txtDia)
        private val viewMarcador: View = itemView.findViewById(R.id.viewMarcador)

        fun vincular(dia: DiaCalendario, aoSelecionarDia: (Date) -> Unit) {
            val contexto = itemView.context
            txtDia.text = String.format("%02d", dia.dia)

            val corTexto = when {
                dia.selecionado -> R.color.white
                dia.doMesExibido -> R.color.agenda_texto
                else -> R.color.hint_color
            }
            txtDia.setTextColor(ContextCompat.getColor(contexto, corTexto))
            txtDia.setBackgroundResource(
                if (dia.selecionado) R.drawable.bg_dia_selecionado else 0
            )

            viewMarcador.visibility =
                if (dia.temConsulta && !dia.selecionado) View.VISIBLE else View.GONE

            itemView.setOnClickListener { aoSelecionarDia(dia.data) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<DiaCalendario>() {
        override fun areItemsTheSame(oldItem: DiaCalendario, newItem: DiaCalendario) =
            oldItem.data == newItem.data

        override fun areContentsTheSame(oldItem: DiaCalendario, newItem: DiaCalendario) =
            oldItem == newItem
    }
}
