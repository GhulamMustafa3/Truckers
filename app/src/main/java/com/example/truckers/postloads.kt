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


class postloads : Fragment() {

    private lateinit var noLoadsImage: ImageView
    private lateinit var noLoadsText: TextView
    private lateinit var  recyclerView: RecyclerView
    private lateinit var loadarraylist:ArrayList<loaddata>
    private lateinit var loadcardadapter:loadcardadapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_postloads, container, false)
        recyclerView = view.findViewById(R.id.load_recycler_view)

        val addloadButton: FloatingActionButton = view.findViewById(R.id.add_load)
        noLoadsImage = view.findViewById(R.id.no_loads_image)
        noLoadsText = view.findViewById(R.id.no_loads_text)
        // Initialize Firebase Database


        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.setHasFixedSize(true)
        loadarraylist= arrayListOf<loaddata>()

        loaddata()
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


    private fun loaddata() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid // Get the UID of the current user
            val database = FirebaseDatabase.getInstance().getReference("shippers")

            // Reference the user's trucks node
            val trucksRef = database.child(userId).child("loaddetails")

            trucksRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    loadarraylist.clear() // Clear the existing list before adding new data

                    if (snapshot.exists()) {
                        for (loadsnap in snapshot.children) {
                            // Deserialize the truck data into truckdata objects
                            val loaddetail = loadsnap.getValue(loaddata::class.java)
                            if (loaddetail != null) {
                                loadarraylist.add(loaddetail)
                            }
                        }

                        // Set the adapter with the updated truck list
                        recyclerView.adapter = loadcardadapter(loadarraylist)

                        // Update visibility based on data availability
                        if (loadarraylist.isNotEmpty()) {
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