package com.example.truckers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TruckCardAdapter(private val truckarraylist: ArrayList<truckdata>) : RecyclerView.Adapter<TruckCardAdapter.TruckViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TruckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_truckcard, parent, false)
        return TruckViewHolder(view)
    }

    override fun onBindViewHolder(holder: TruckViewHolder, position: Int) {
        val truck = truckarraylist[position]

        holder.source.text=truck.origin
        holder.destination.text=truck.destination
        holder.weight.text="${truck.weight} KG"
        holder.length.text="${truck.length} ft"
        holder.loadType.text="Limits:${truck.limits}"
        holder.truck_type.text="Type:${truck.type}"


    }

    override fun getItemCount(): Int = truckarraylist.size

    class TruckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         val source = itemView.findViewById<TextView>(R.id.source)
         val destination = itemView.findViewById<TextView>(R.id.destination)
         val weight = itemView.findViewById<TextView>(R.id.weight)
         val length = itemView.findViewById<TextView>(R.id.length)
         val loadType = itemView.findViewById<TextView>(R.id.load_type)
        val truck_type=itemView.findViewById<TextView>(R.id.truck_type)



    }
}