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
        // Initialize Firebase Database


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
                loadarraylist.clear() // Clear the existing list before adding new data

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) { // Loop through all users
                        val loadsRef = userSnapshot.child("loaddetails") // Get loaddetails for each user

                        for (loadSnap in loadsRef.children) {
                            val loaddetail = loadSnap.getValue(loaddata::class.java)
                            if (loaddetail != null) {
                                loadarraylist.add(loaddetail)
                            }
                        }
                    }

                    // Set the adapter with the updated truck list
                    recyclerView.adapter = loadcardadapter(loadarraylist)

                    // Handle item click to open LoadDetailsFragment
                    (recyclerView.adapter as loadcardadapter).setOnItemClickListener(object : loadcardadapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            // Get the selected load details
                            val selectedLoad = loadarraylist[position]

                            // Create a bundle and add selected load details to it
                            val bundle = Bundle().apply {
                                putString("destination", selectedLoad.destination)
                                putString("dropoffDate", selectedLoad.dropoffDate)
                                putString("dropoffTime", selectedLoad.dropoffTime)
                                putString("length", selectedLoad.length)
                                putString("limits", selectedLoad.limits)
                                putString("material", selectedLoad.material)
                                putString("origin", selectedLoad.origin)
                                putString("phone", selectedLoad.phone)
                                putString("pickupDate", selectedLoad.pickupDate)
                                putString("pickupTime", selectedLoad.pickupTime)
                                putString("price", selectedLoad.price)
                                putString("truckType", selectedLoad.truckType)
                            }

                            // Pass the bundle to LoadCompletedDetailsFragment
                            val loadCompletedDetailsFragment = loadcompletedetails().apply {
                                arguments = bundle
                            }

                            // Perform the fragment transaction
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.container, loadCompletedDetailsFragment) // Replace container with the new fragment
                                .addToBackStack(null) // Add to back stack if needed
                                .commit()
                        }
                    })

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
                    // Handle the case where no loads are available
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