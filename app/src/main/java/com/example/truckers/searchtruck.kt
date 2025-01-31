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


class searchtruck : Fragment() {


    private lateinit var  recyclerView: RecyclerView
    private lateinit var noLoadsImage: ImageView
    private lateinit var noLoadsText: TextView
    private lateinit var truckarraylist:ArrayList<truckdata>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_searchtruck, container, false)

        recyclerView = view.findViewById(R.id.searchtruck_recycler_view)


        noLoadsImage = view.findViewById(R.id.no_loads_image)
        noLoadsText = view.findViewById(R.id.no_loads_text)
        // Initialize Firebase Database


        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.setHasFixedSize(true)

        truckarraylist= arrayListOf<truckdata> ()
        // Fetch truck data from Firebase


        return view
    }


}