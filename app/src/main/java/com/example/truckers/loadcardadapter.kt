package com.example.truckers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class loadcardadapter(private val loadlist: ArrayList<loaddata>) : RecyclerView.Adapter<loadcardadapter.loadViewHolder>() {

    private var mlistener: onItemClickListener? = null

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mlistener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): loadViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_loadcard, parent, false)
        return loadViewHolder(view, mlistener)
    }
    fun deleteitem(i:Int){
        loadlist.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: loadViewHolder, position: Int) {
        val load = loadlist[position]
        holder.origin.text = load.origin
        holder.destination.text = load.destination
        holder.weight.text = "${load.weight} kg"


        holder.length.text = "${load.length} ft"
        holder.loadType.text ="Limits: ${load.limits}"
        holder.pickupDate.text = load.pickupDate
        holder.price.text = load.price
    }

    override fun getItemCount(): Int = loadlist.size

    class loadViewHolder(itemView: View, listener: onItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        val origin: TextView = itemView.findViewById(R.id.origin)
        val destination: TextView = itemView.findViewById(R.id.destination)
        val weight: TextView = itemView.findViewById(R.id.weight)
        val length: TextView = itemView.findViewById(R.id.dimension)
        val loadType: TextView = itemView.findViewById(R.id.load_type)
        val pickupDate: TextView = itemView.findViewById(R.id.date)
        val price: TextView = itemView.findViewById(R.id.price)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position)
                }
            }
        }
    }
}
