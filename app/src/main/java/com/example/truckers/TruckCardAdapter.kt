package com.example.truckers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TruckCardAdapter(private val truckList: List<truckdata>) : RecyclerView.Adapter<TruckCardAdapter.TruckViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TruckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_truckcard, parent, false)
        return TruckViewHolder(view)
    }

    override fun onBindViewHolder(holder: TruckViewHolder, position: Int) {
        val truck = truckList[position]
        holder.companyNameText.text=truck.username
        holder.sourceText.text=truck.origin
        holder.destinationText.text=truck.destination
        holder.weightText.text=truck.weight
        holder.dimensionText.text=truck.length
        holder.loadTypeText.text=truck.type


    }

    override fun getItemCount(): Int = truckList.size

    class TruckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val companyNameText = itemView.findViewById<TextView>(R.id.company_name)
         val sourceText = itemView.findViewById<TextView>(R.id.source)
         val destinationText = itemView.findViewById<TextView>(R.id.destination)
         val weightText = itemView.findViewById<TextView>(R.id.weight)
         val dimensionText = itemView.findViewById<TextView>(R.id.length)
         val loadTypeText = itemView.findViewById<TextView>(R.id.load_type)


    }
}