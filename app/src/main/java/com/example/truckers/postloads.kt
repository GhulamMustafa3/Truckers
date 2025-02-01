package com.example.truckers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
            val userId = currentUser.uid
            val database = FirebaseDatabase.getInstance().getReference("shippers")
            val loadsRef = database.child(userId).child("loaddetails")

            loadsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    loadarraylist.clear()

                    if (snapshot.exists()) {
                        for (loadsnap in snapshot.children) {
                            val loaddetail = loadsnap.getValue(loaddata::class.java)
                            loaddetail?.let { loadarraylist.add(it) }
                        }

                        // Set the adapter after loading data
                        loadcardadapter = loadcardadapter(loadarraylist)
                        recyclerView.adapter = loadcardadapter

                        // Handle item click
                        loadcardadapter.setOnItemClickListener(object : loadcardadapter.onItemClickListener {
                            override fun onItemClick(position: Int) {
                                // Get the selected load details
                                val selectedLoad = loadarraylist[position]

                                // Create a bundle and add selected load details to it
                                val bundle = Bundle()
                                bundle.putString("destination", selectedLoad.destination)
                                bundle.putString("dropoffDate", selectedLoad.dropoffDate)
                                bundle.putString("dropoffTime", selectedLoad.dropoffTime)
                                bundle.putString("length", selectedLoad.length)
                                bundle.putString("limits", selectedLoad.limits)
                                bundle.putString("material", selectedLoad.material)
                                bundle.putString("origin", selectedLoad.origin)
                                bundle.putString("phone", selectedLoad.phone)
                                bundle.putString("pickupDate", selectedLoad.pickupDate)
                                bundle.putString("pickupTime", selectedLoad.pickupTime)
                                bundle.putString("price", selectedLoad.price)
                                bundle.putString("truckType", selectedLoad.truckType)

                                // Pass the bundle to LoadCompletedDetailsFragment
                                val loadCompletedDetailsFragment = loadcompletedetails()
                                loadCompletedDetailsFragment.arguments = bundle

                                // Perform the fragment transaction
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.container, loadCompletedDetailsFragment) // Replace container with the new fragment
                                    .addToBackStack(null) // Add to back stack if needed
                                    .commit()
                            }
                        })

                        // Update UI visibility based on data
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
                        recyclerView.visibility = View.GONE
                        noLoadsImage.visibility = View.VISIBLE
                        noLoadsText.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


}