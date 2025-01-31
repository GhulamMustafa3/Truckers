package com.example.truckers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class loadcardadapter(private val loadlist:ArrayList<loaddata>):RecyclerView.Adapter<loadcardadapter.loadViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): loadcardadapter.loadViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.fragment_loadcard,parent,false)
        return loadViewHolder(view)

    }

    override fun onBindViewHolder(holder: loadcardadapter.loadViewHolder, position: Int) {
        val load=loadlist[position]
        holder.origin.text=load.origin
        holder.destination.text=load.destination
        holder.weight.text=load.weight
        holder.length.text=load.length
        holder.loadType.text=load.limits
        holder.pickupDate.text=load.pickupDate
        holder.price.text=load.price
        
    }

    override fun getItemCount(): Int=loadlist.size
    
    class loadViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val origin = itemView.findViewById<TextView>(R.id.origin)
        val destination = itemView.findViewById<TextView>(R.id.destination)
        val weight = itemView.findViewById<TextView>(R.id.weight)
        val length = itemView.findViewById<TextView>(R.id.dimension)
        val loadType = itemView.findViewById<TextView>(R.id.load_type)
        val pickupDate=itemView.findViewById<TextView>(R.id.date)
        val price=itemView.findViewById<TextView>(R.id.price)


    }
}