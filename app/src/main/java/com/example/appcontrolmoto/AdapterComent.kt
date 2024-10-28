package com.example.appcontrolmoto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class AdapterComent(private var lstComentario: List<Coment>):
    RecyclerView.Adapter<AdapterComent.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val tvUsuario: TextView = itemView.findViewById(R.id.tvUsuario)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvComentario: TextView = itemView.findViewById(R.id.tvComentario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_comentario,parent,false))

    }

    override fun getItemCount(): Int {
        return lstComentario.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemComent = lstComentario[position]
        holder.tvUsuario.text = itemComent.usuario.toString()
        holder.tvFecha.text = itemComent.fecha.toString()
        holder.tvComentario.text = itemComent.comentario.toString()
    }

}
