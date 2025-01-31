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

            // Reference the user's trucks node


            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    truckarraylist.clear() // Clear the existing list before adding new data

                    if (snapshot.exists()) {
                        for (usersnap in snapshot.children) {
                            val truckref=usersnap.child("trucks")
                            for(trucksnap in truckref.children) {
                                val truckdetail = trucksnap.getValue(truckdata::class.java)
                                if (truckdetail != null) {
                                    truckarraylist.add(truckdetail)
                                }
                            }
                        }

                        // Set the adapter with the updated truck list
                        recyclerView.adapter = TruckCardAdapter(truckarraylist)

                        // Update visibility based on data availability
                        if (truckarraylist.isNotEmpty()) {
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



}