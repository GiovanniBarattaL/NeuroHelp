package com.example.neurohelp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.neurohelp.ajuda.FaqItem

class FaqAdapter(
    private val itens: MutableList<FaqItem>
) : RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    inner class FaqViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layoutPergunta: View = view.findViewById(R.id.itemPergunta)
        val txtPergunta: TextView = view.findViewById(R.id.txtPergunta)
        val txtResposta: TextView = view.findViewById(R.id.txtResposta)
        val imgSeta: ImageView = view.findViewById(R.id.imgSeta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faq, parent, false)
        return FaqViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val item = itens[position]

        holder.txtPergunta.text = item.pergunta
        holder.txtResposta.text = item.resposta

        holder.txtResposta.visibility = if (item.expandida) View.VISIBLE else View.GONE
        holder.imgSeta.rotation = if (item.expandida) -90f else 0f

        holder.layoutPergunta.setOnClickListener {
            item.expandida = !item.expandida
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = itens.size
}
