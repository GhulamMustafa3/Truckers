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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class loadboard : Fragment() {
    private lateinit var noLoadsImage: ImageView
    private lateinit var noLoadsText: TextView
    private lateinit var  recyclerView: RecyclerView
    private lateinit var loadarraylist:ArrayList<loaddata>
    private lateinit var advancedfilter:TextView
    private lateinit var refresh:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_loadboard, container, false)
        recyclerView = view.findViewById(R.id.load_recycler_view)
        noLoadsImage = view.findViewById(R.id.no_loads_image)
        noLoadsText = view.findViewById(R.id.no_loads_text)
        advancedfilter=view.findViewById(R.id.searchfilter)
        refresh=view.findViewById(R.id.refresh)

        // Initialize Firebase Database

advancedfilter.setOnClickListener{
    requireActivity().supportFragmentManager.beginTransaction()
        .replace(R.id.container, searchload()) // Replace container with the new fragment
        .addToBackStack(null) // Add to back stack if needed
        .commit()
}
        refresh.setOnClickListener{
            arguments = null // Clear filters
            loaddata()
        }
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.setHasFixedSize(true)
        loadarraylist= arrayListOf<loaddata>()

        loaddata()
        return view
    }



    private fun loaddata() {
        val database = FirebaseDatabase.getInstance().getReference("shippers")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loadarraylist.clear() // Clear previous data

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val loadsRef = userSnapshot.child("loaddetails")

                        for (loadSnap in loadsRef.children) {
                            val loaddetail = loadSnap.getValue(loaddata::class.java)
                            if (loaddetail != null) {
                                loadarraylist.add(loaddetail)
                            }
                        }
                    }

                    var displayedList = ArrayList(loadarraylist) // Default list

                    // Apply filters if arguments exist
                    arguments?.let {
                        displayedList = loadarraylist.filter { load ->
                            (it.getString("origin").isNullOrEmpty() || load.origin == it.getString("origin")) &&
                                    (it.getString("destination").isNullOrEmpty() || load.destination == it.getString("destination")) &&
                                    (it.getString("startDate").isNullOrEmpty() || load.pickupDate == it.getString("startDate")) &&
                                    (it.getString("endDate").isNullOrEmpty() || load.dropoffDate == it.getString("endDate")) &&
                                    (it.getString("length").isNullOrEmpty() || load.length == it.getString("length")) &&
                                    (it.getString("weight").isNullOrEmpty() || load.weight == it.getString("weight")) &&
                                    (it.getString("limits").isNullOrEmpty() || load.limits == it.getString("limits")) &&
                                    (it.getString("truckType").isNullOrEmpty() || load.truckType == it.getString("truckType"))
                        } as ArrayList<loaddata>
                    }

                    // Set adapter with displayed list
                    val adapter = loadcardadapter(displayedList)
                    recyclerView.adapter = adapter

                    adapter.setOnItemClickListener(object : loadcardadapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            openLoadCompleteDetails(displayedList[position])
                        }
                    })

                    // Handle visibility based on list size
                    if (displayedList.isNotEmpty()) {
                        recyclerView.visibility = View.VISIBLE
                        noLoadsImage.visibility = View.GONE
                        noLoadsText.visibility = View.GONE
                    } else {
                        recyclerView.visibility = View.GONE
                        noLoadsImage.visibility = View.VISIBLE
                        noLoadsText.visibility = View.VISIBLE
                    }
                } else {
                    recyclerView.visibility = View.GONE
                    noLoadsImage.visibility = View.VISIBLE
                    noLoadsText.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun openLoadCompleteDetails(load: loaddata) {
        val bundle = Bundle().apply {
            putString("destination", load.destination)
            putString("dropoffDate", load.dropoffDate)
            putString("dropoffTime", load.dropoffTime)
            putString("length", load.length)
            putString("limits", load.limits)
            putString("material", load.material)
            putString("origin", load.origin)
            putString("phone", load.phone)
            putString("pickupDate", load.pickupDate)
            putString("pickupTime", load.pickupTime)
            putString("price", load.price)
            putString("truckType", load.truckType)
        }

        val loadCompletedDetailsFragment = loadcompletedetails().apply {
            arguments = bundle
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, loadCompletedDetailsFragment) // Ensure this ID exists
            .addToBackStack(null)
            .commit()
    }






}