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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class searchtruck : Fragment() {


    private lateinit var  recyclerView: RecyclerView
    private lateinit var noLoadsImage: ImageView
    private lateinit var noLoadsText: TextView
    private lateinit var truckarraylist:ArrayList<truckdata>
    private lateinit var advancedfilter:TextView
    private lateinit var refresh:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_searchtruck, container, false)

        recyclerView = view.findViewById(R.id.truck_recycler_view)


        noLoadsImage = view.findViewById(R.id.no_loads_image)
        noLoadsText = view.findViewById(R.id.no_loads_text)
        // Initialize Firebase Database

        advancedfilter=view.findViewById(R.id.searchfilter)
        refresh=view.findViewById(R.id.refresh)

        advancedfilter.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, searchtruckfilters()) // Replace container with the new fragment
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

        truckarraylist= arrayListOf<truckdata> ()
        // Fetch truck data from Firebase
        loaddata()

        return view
    }
    private fun loaddata() {
        val database = FirebaseDatabase.getInstance().getReference("users")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                truckarraylist.clear() // Clear the existing list before adding new data

                if (snapshot.exists()) {
                    for (usersnap in snapshot.children) {
                        val phone = usersnap.child("phone").getValue(String::class.java) ?: "N/A" // Get user phone
                        val truckref = usersnap.child("trucks")

                        for (trucksnap in truckref.children) {
                            val truckdetail = trucksnap.getValue(truckdata::class.java)
                            if (truckdetail != null) {
                                truckdetail.phone = phone // Attach phone number to truck data

                                // Get the truck's unique Firebase ID (auto-generated key)
                                val truckID = trucksnap.key ?: "Unknown ID" // Firebase automatically generates this key
                                truckdetail.truckID = truckID // Assign the truck ID to the truck data object

                                truckarraylist.add(truckdetail)
                            }
                        }
                    }

                    var displayedList = ArrayList(truckarraylist)

                    // Apply filters if arguments exist
                    arguments?.let {
                        displayedList = truckarraylist.filter { load ->
                            (it.getString("origin").isNullOrEmpty() || load.origin == it.getString("origin")) &&
                                    (it.getString("destination").isNullOrEmpty() || load.destination == it.getString("destination")) &&
                                    (it.getString("length").isNullOrEmpty() || load.length == it.getString("length")) &&
                                    (it.getString("weight").isNullOrEmpty() || load.weight == it.getString("weight")) &&
                                    (it.getString("limits").isNullOrEmpty() || load.limits == it.getString("limits")) &&
                                    (it.getString("truckType").isNullOrEmpty() || load.type == it.getString("truckType"))
                        } as ArrayList<truckdata>
                    }

                    // Set adapter with displayed list
                    val adapter = TruckCardAdapter(displayedList)

                    recyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object : TruckCardAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val truckdetail = displayedList[position]
                            openTruckCompleteDetails(truckdetail, truckdetail.truckID!!) // Pass the truck ID
                        }
                    })

                    // Update visibility based on data availability
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
                    // Handle the case where no trucks are available
                    recyclerView.visibility = View.GONE
                    noLoadsImage.visibility = View.VISIBLE
                    noLoadsText.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors during data retrieval
            }
        })
    }





    private fun openTruckCompleteDetails(load: truckdata, truckID: String) {
        val bundle = Bundle().apply {
            putString("truckID", truckID) // Add the truck ID to the bundle
            putString("destination", load.destination)
            putString("endDate", load.endDate)
            putString("weight", "KG:${load.weight}")
            putString("length", "Height:${load.length} ft")
            putString("limits", "Limits: ${load.limits}")
            putString("origin", load.origin)
            putString("phone", load.phone)
            putString("startDate", load.startDate)
            putString("truckType", load.type)
        }

        val truckCompletedDetailsFragment = truckcompletedetails().apply {
            arguments = bundle
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, truckCompletedDetailsFragment) // Ensure this ID exists
            .addToBackStack(null)
            .commit()
    }




}