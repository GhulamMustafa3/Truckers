package com.example.truckers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class truckcompletedetails : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_truckcompletedetails, container, false)
        val destination = arguments?.getString("destination")
        val startDate = arguments?.getString("startDate")

        val length = arguments?.getString("length")
        val limits = arguments?.getString("limits")
       val weight=arguments?.getString("weight")
        val origin = arguments?.getString("origin")
        val phone = arguments?.getString("phone")
        val endDate = arguments?.getString("endDate")


        val Type = arguments?.getString("Type")

        // Find the views and set values
        view.findViewById<TextView>(R.id.source).text = origin
        view.findViewById<TextView>(R.id.destination).text = destination

        view.findViewById<TextView>(R.id.Pickupdate).text = startDate
        view.findViewById<TextView>(R.id.length).text = length
        view.findViewById<TextView>(R.id.truck_type).text = Type
        view.findViewById<TextView>(R.id.weight).text=weight
        view.findViewById<TextView>(R.id.Dropoffdate).text=endDate

        view.findViewById<TextView>(R.id.load_type).text=limits
        view.findViewById<TextView>(R.id.Phone).text=phone

        return view
    }


}