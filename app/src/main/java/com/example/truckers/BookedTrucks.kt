package com.example.truckers

import android.annotation.SuppressLint
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


class BookedTrucks : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadArrayList: ArrayList<truckdata>
    private lateinit var adapter: TruckCardAdapter
    private lateinit var noLoadsImage: ImageView
    private lateinit var noLoadsText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_booked_trucks, container, false)
        recyclerView = view.findViewById(R.id.truck_recycler_view)
        noLoadsImage = view.findViewById(R.id.no_loads_image)
        noLoadsText = view.findViewById(R.id.no_loads_text)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        loadArrayList = ArrayList()
        adapter =TruckCardAdapter(loadArrayList)
        recyclerView.adapter = adapter
        loadBookedTrucks()

        return view
    }
    private fun loadBookedTrucks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("shippers")
            .child(userId).child("bookedTrucks")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loadArrayList.clear()
                if (snapshot.exists()) {
                    for (loadSnap in snapshot.children) {
                        val loaddetail = loadSnap.getValue(truckdata::class.java)
                        if (loaddetail != null) {
                            loadArrayList.add(loaddetail)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                if (loadArrayList.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    noLoadsImage.visibility = View.GONE
                    noLoadsText.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.GONE
                    noLoadsImage.visibility = View.VISIBLE
                    noLoadsText.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}