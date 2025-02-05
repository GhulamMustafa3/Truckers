package com.example.truckers

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class loadcompletedetails : Fragment(), OnMapReadyCallback {
    private lateinit var phoneTextView: TextView
    private lateinit var callButton: Button
    private lateinit var bookNowButton:Button
    private lateinit var mMap: GoogleMap
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loadcompletedetails, container, false)
        phoneTextView = view.findViewById(R.id.Phone) // Get phone number TextView
        callButton = view.findViewById(R.id.call)     // Get Call button
        bookNowButton=view.findViewById(R.id.book)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        callButton.setOnClickListener {
            makePhoneCall()
        }
        bookNowButton.setOnClickListener {
            bookLoad()
        }

        // Retrieve arguments from bundle
        val destination = arguments?.getString("destination")
        val dropoffDate = arguments?.getString("dropoffDate")
        val dropoffTime = arguments?.getString("dropoffTime")
        val length = arguments?.getString("length")
        val limits = arguments?.getString("limits")
        val material = arguments?.getString("material")
        val origin = arguments?.getString("origin")
        val phone = arguments?.getString("phone")
        val pickupDate = arguments?.getString("pickupDate")
        val pickupTime = arguments?.getString("pickupTime")
        val price = arguments?.getString("price")
        val truckType = arguments?.getString("truckType")

        // Find the views and set values
        view.findViewById<TextView>(R.id.pickuploc).text = origin
        view.findViewById<TextView>(R.id.dropoffloc).text = destination
        view.findViewById<TextView>(R.id.Price).text = price
        view.findViewById<TextView>(R.id.Pickupdate).text = pickupDate
        view.findViewById<TextView>(R.id.length).text = length
        view.findViewById<TextView>(R.id.trucktype).text = truckType
        view.findViewById<TextView>(R.id.Material).text = material
        view.findViewById<TextView>(R.id.Dropoffdate).text=dropoffDate
        view.findViewById<TextView>(R.id.dropofftime).text=dropoffTime
        view.findViewById<TextView>(R.id.equiptype).text=limits
        view.findViewById<TextView>(R.id.Phone).text=phone
        view.findViewById<TextView>(R.id.PickupTime).text=pickupTime

        return view
    }
    private fun makePhoneCall() {
        val phoneNumber = phoneTextView.text.toString().trim()

        if (phoneNumber.isNotEmpty()) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")

            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                if (callIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(callIntent)
                } else {
                    Toast.makeText(requireContext(), "No app found to handle calls", Toast.LENGTH_SHORT).show()
                }
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_CALL_PHONE)
            }
        } else {
            Toast.makeText(requireContext(), "Phone number not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bookLoad() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        val databaseRef = FirebaseDatabase.getInstance().getReference("shippers")
            .child(userId)
            .child("bookedloadsreq")

        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val bookid = databaseRef.push().key

        val bookedLoadsRef = userRef.child("bookedLoads")
        val loadId = bookedLoadsRef.push().key // Generate unique key for the booked load
        val loadIds = arguments?.getString("loadId") ?: return

        userRef.get().addOnSuccessListener { snapshot ->
            val userPhone = snapshot.child("phone").getValue(String::class.java) ?: ""

            val load = loaddata(
                destination = arguments?.getString("destination") ?: "",
                dropoffDate = arguments?.getString("dropoffDate") ?: "",
                dropoffTime = arguments?.getString("dropoffTime") ?: "",
                length = arguments?.getString("length") ?: "",
                limits = arguments?.getString("limits") ?: "",
                material = arguments?.getString("material") ?: "",
                origin = arguments?.getString("origin") ?: "",
                phone = userPhone, // Store the current user's phone
                pickupDate = arguments?.getString("pickupDate") ?: "",
                pickupTime = arguments?.getString("pickupTime") ?: "",
                price = arguments?.getString("price") ?: "",
                truckType = arguments?.getString("truckType") ?: "",
                loadId = loadIds,
                email = userEmail // Store the current user's email
            )

            // Store in the current user's booked loads
            if (loadId != null) {
                bookedLoadsRef.child(loadId).setValue(load)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Load booked successfully!", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to book load", Toast.LENGTH_SHORT).show()
                    }
            }

            // Store in shipper's booked load requests
            if (bookid != null) {
                val loadRef = FirebaseDatabase.getInstance().getReference("shippers")
                loadRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (usersnap in snapshot.children) {
                            val shipperId = usersnap.key
                            val loads = usersnap.child("loaddetails")
                            for (loadsnap in loads.children) {
                                val loaddata = loadsnap.getValue(loaddata::class.java)
                                if (loaddata != null && loadsnap.key == loadIds) {
                                    val loaddetails = FirebaseDatabase.getInstance()
                                        .getReference("shippers")
                                        .child(shipperId!!)
                                        .child("bookedloadsreq")
                                        .push()

                                    // Store with user details
                                    val bookedLoadWithUser = loaddata.copy(
                                        phone = userPhone,
                                        email = userEmail
                                    )

                                    loaddetails.setValue(bookedLoadWithUser)
                                        .addOnSuccessListener { navigateToMyLoads() }
                                        .addOnFailureListener {
                                            Toast.makeText(requireContext(), "Failed to book load request", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }



    private fun navigateToMyLoads() {
        val myLoadsFragment = myloads()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, myLoadsFragment)
            .addToBackStack(null)
            .commit()
    }
    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
       private const val REQUEST_CALL_PHONE = 1
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Retrieve origin and destination from arguments
        val origin = arguments?.getString("origin") ?: "New York"
        val destination = arguments?.getString("destination") ?: "Los Angeles"

        // Convert to LatLng (You need a geocoding service to get exact coordinates)
        val originLatLng = LatLng(33.6844, 73.0479) // Example: Islamabad
        val destinationLatLng = LatLng(31.5497, 74.3436) // Example: Lahore

        mMap.addMarker(MarkerOptions().position(originLatLng).title("Pickup Location: $origin"))
        mMap.addMarker(MarkerOptions().position(destinationLatLng).title("Drop-off Location: $destination"))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originLatLng, 10f))
    }

}
