package com.example.truckers

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class mytruck : Fragment() {

    private lateinit var database: DatabaseReference

    private lateinit var  recyclerView: RecyclerView
    private lateinit var noLoadsImage: ImageView
    private lateinit var noLoadsText: TextView
   private lateinit var truckarraylist:ArrayList<truckdata>

    private val truckList = arrayListOf<truckdata>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mytruck, container, false)

        recyclerView = view.findViewById(R.id.truck_recycler_view)

        val addTruckButton: FloatingActionButton = view.findViewById(R.id.add_truck)
        noLoadsImage = view.findViewById(R.id.no_loads_image)
        noLoadsText = view.findViewById(R.id.no_loads_text)
        // Initialize Firebase Database


        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

       recyclerView.setHasFixedSize(true)

        truckarraylist= arrayListOf<truckdata> ()
        // Fetch truck data from Firebase
       loaddata()

        // Handle FloatingActionButton click
        addTruckButton.setOnClickListener {
            openTruckDetailsFragment()
        }


        return view
    }


    private fun openTruckDetailsFragment() {
        val truckDetailsFragment = truckdetails()
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, truckDetailsFragment) // Ensure this ID matches your container
            .addToBackStack(null) // Adds the transaction to the back stack for navigation
            .commit()
    }


    private fun loaddata() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid // Get the UID of the current user
            val database = FirebaseDatabase.getInstance().getReference("users")

            // Reference the user's trucks node
            val trucksRef = database.child(userId).child("trucks")

            trucksRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    truckarraylist.clear() // Clear the existing list before adding new data

                    if (snapshot.exists()) {
                        for (trucksnap in snapshot.children) {
                            // Deserialize the truck data into truckdata objects
                            val truckdetail = trucksnap.getValue(truckdata::class.java)
                            if (truckdetail != null) {
                                truckarraylist.add(truckdetail)
                            }
                        }

                        // Set the adapter with the updated truck list
                        val adapter = TruckCardAdapter(truckarraylist)
                        val swipeGesture= object : swipeGesture(requireContext()) {
                            override fun onSwiped(
                                viewHolder: RecyclerView.ViewHolder,
                                direction: Int
                            ) {

                                when(direction){
                                    ItemTouchHelper.LEFT->{
                                     adapter.deleteitem(viewHolder.adapterPosition)
                                    }
                                }

                            }

                        }
                        val touchHelper=ItemTouchHelper(swipeGesture)
                        touchHelper.attachToRecyclerView(recyclerView)
                        recyclerView.adapter=adapter

                        adapter.setOnItemClickListener(object :TruckCardAdapter.onItemClickListener{
                            override fun onItemClick(position:Int){

                            }
                        })



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
        } else {

        }
    }



}
