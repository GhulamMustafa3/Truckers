package com.example.truckers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton



class postloads : Fragment() {

    private lateinit var noLoadsImage: ImageView
    private lateinit var noLoadsText: TextView
    private lateinit var  recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_postloads, container, false)
        recyclerView = view.findViewById(R.id.truck_recycler_view)

        val addloadButton: FloatingActionButton = view.findViewById(R.id.add_load)
        noLoadsImage = view.findViewById(R.id.no_loads_image)
        noLoadsText = view.findViewById(R.id.no_loads_text)
        // Initialize Firebase Database


        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.setHasFixedSize(true)
        addloadButton.setOnClickListener{
            openloadDetailsFragment()
        }

        return view
    }

    private fun openloadDetailsFragment() {
        val loadDetailsFragment = loaddetailsform()
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, loadDetailsFragment) // Ensure this ID matches your container
            .addToBackStack(null) // Adds the transaction to the back stack for navigation
            .commit()
    }



}