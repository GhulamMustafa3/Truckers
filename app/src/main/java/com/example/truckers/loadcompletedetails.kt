package com.example.truckers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class loadcompletedetails : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loadcompletedetails, container, false)

        // Retrieve arguments from bundle
        val destination = arguments?.getString("destination")
        val dropoffDate = arguments?.getString("dropoffDate")
        val dropoffTime = arguments?.getString("dropoffTime")
        val length = arguments?.getString("length")
        val limits = arguments?.getString("limits")
        val material = arguments?.getString("material")
        val origin = arguments?.getString("origin")
        val phone = arguments?.getString("phone")
        val pickupDate = arguments?.getString("pickupDate")
        val pickupTime = arguments?.getString("pickupTime")
        val price = arguments?.getString("price")
        val truckType = arguments?.getString("truckType")

        // Find the views and set values
        view.findViewById<TextView>(R.id.pickuploc).text = origin
        view.findViewById<TextView>(R.id.dropoffloc).text = destination
        view.findViewById<TextView>(R.id.Price).text = price
        view.findViewById<TextView>(R.id.Pickupdate).text = pickupDate
        view.findViewById<TextView>(R.id.length).text = length
        view.findViewById<TextView>(R.id.trucktype).text = truckType
        view.findViewById<TextView>(R.id.Material).text = material
        view.findViewById<TextView>(R.id.Dropoffdate).text=dropoffDate
        view.findViewById<TextView>(R.id.dropofftime).text=dropoffTime
        view.findViewById<TextView>(R.id.equiptype).text=limits
        view.findViewById<TextView>(R.id.Phone).text=phone
        view.findViewById<TextView>(R.id.PickupTime).text=pickupTime

        return view
    }
}
