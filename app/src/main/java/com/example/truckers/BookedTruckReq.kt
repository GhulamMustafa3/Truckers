package com.example.truckers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BookedTruckReq : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noLoadsImage: ImageView
    private lateinit var noLoadsText: TextView
    private lateinit var truckArrayList: ArrayList<truckdata>
    private lateinit var adapter: TruckCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booked_truck_req, container, false)
        recyclerView = view.findViewById(R.id.truck_recycler_view)
        noLoadsImage = view.findViewById(R.id.no_loads_image)
        noLoadsText = view.findViewById(R.id.no_loads_text)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        truckArrayList = arrayListOf()
        adapter = TruckCardAdapter(truckArrayList)
        recyclerView.adapter = adapter

        loadBookedTrucks() // Load data from Firebase

        return view
    }

    private fun loadBookedTrucks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("bookedtruckreq") // Access booked trucks under current user

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                truckArrayList.clear()

                if (snapshot.exists()) {
                    for (truckSnap in snapshot.children) {
                        val truckDetail = truckSnap.getValue(truckdata::class.java)

                        // ✅ Fetch email & phone directly from truckSnap
                        val shipperEmail = truckSnap.child("email").getValue(String::class.java) ?: ""
                        val shipperPhone = truckSnap.child("phone").getValue(String::class.java) ?: ""

                        if (truckDetail != null) {
                            truckArrayList.add(truckDetail)
                        }
                    }

                    adapter.notifyDataSetChanged()
                    adapter.setOnItemClickListener(object : TruckCardAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val selectedTruck = truckArrayList[position]

                            // ✅ Pass email and phone when navigating to ShipperDetailsFragment
                            val bundle = Bundle().apply {
                                putString("shipper_email", snapshot.children.elementAt(position).child("email").getValue(String::class.java))
                                putString("shipper_phone", snapshot.children.elementAt(position).child("phone").getValue(String::class.java))
                            }

                            val shipperDetailsFragment = shipperdetails()
                            shipperDetailsFragment.arguments = bundle

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.container, shipperDetailsFragment) // Adjust with your container ID
                                .addToBackStack(null)
                                .commit()
                        }
                    })

                    if (truckArrayList.isNotEmpty()) {
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
