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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PickupReq : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noLoadsImage: ImageView
    private lateinit var noLoadsText: TextView
    private lateinit var loadArrayList: ArrayList<loaddata>
    private lateinit var adapter: loadcardadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_pickup_req, container, false)
        recyclerView = view.findViewById(R.id.truck_recycler_view)
        noLoadsImage = view.findViewById(R.id.no_loads_image)
        noLoadsText = view.findViewById(R.id.no_loads_text)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        loadArrayList = arrayListOf()
        adapter = loadcardadapter(loadArrayList)
        recyclerView.adapter = adapter

        loadBookedLoads()
        return view
    }

    private fun loadBookedLoads() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("shippers")
            .child(userId)
            .child("bookedloadsreq") // Access booked trucks under current user

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loadArrayList.clear()

                if (snapshot.exists()) {
                    for (truckSnap in snapshot.children) {
                        val truckDetail = truckSnap.getValue(loaddata::class.java)
                        val shipperEmail = truckSnap.child("email").getValue(String::class.java) ?: ""
                        val shipperPhone = truckSnap.child("phone").getValue(String::class.java) ?: ""
                        if (truckDetail != null) {
                            loadArrayList.add(truckDetail)
                        }
                    }

                    adapter.notifyDataSetChanged()
                    adapter.setOnItemClickListener(object : loadcardadapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val selectedTruck = loadArrayList[position]

                            // ✅ Pass email and phone when navigating to ShipperDetailsFragment
                            val bundle = Bundle().apply {
                                putString("email", snapshot.children.elementAt(position).child("email").getValue(String::class.java))
                                putString("phone", snapshot.children.elementAt(position).child("phone").getValue(String::class.java))
                            }

                            val shipperDetailsFragment =DRIVERDETAILS()
                            shipperDetailsFragment.arguments = bundle

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.container, shipperDetailsFragment) // Adjust with your container ID
                                .addToBackStack(null)
                                .commit()
                        }
                    })
                    if (loadArrayList.isNotEmpty()) {
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
                Toast.makeText(requireContext(), "Failed to load booked trucks", Toast.LENGTH_SHORT).show()
            }
        })
    }


}