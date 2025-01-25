package com.example.truckers

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class mytruck : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mytruck, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.truck_recycler_view)
        val noLoadsImage: View = view.findViewById(R.id.no_loads_image)
        val noLoadsText: View = view.findViewById(R.id.no_loads_text)
        val addTruckButton: FloatingActionButton = view.findViewById(R.id.add_truck)

        // Setup RecyclerView or show "No Trucks" message
        val trucks = getTrucks() // Replace with your data source logic
        if (trucks.isEmpty()) {
            recyclerView.visibility = View.GONE
            noLoadsImage.visibility = View.VISIBLE
            noLoadsText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noLoadsImage.visibility = View.GONE
            noLoadsText.visibility = View.GONE
            // Setup RecyclerView adapter here
        }

        // Handle FloatingActionButton click
        addTruckButton.setOnClickListener {
            val intent = Intent(requireContext(), vehicleinfo::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun getTrucks(): List<String> {
        // Replace with your logic to fetch trucks
        return emptyList()
    }
}
