package com.example.truckers

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class truckcompletedetails : Fragment() {

    private lateinit var phoneTextView: TextView
    private lateinit var callButton: Button
    private lateinit var bookNowButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_truckcompletedetails, container, false)
        phoneTextView = view.findViewById(R.id.Phone) // Get phone number TextView
        callButton = view.findViewById(R.id.callnow)     // Get Call button
        bookNowButton=view.findViewById(R.id.booknow)
        callButton.setOnClickListener {
            makePhoneCall()
        }
        bookNowButton.setOnClickListener {
            bookTruck()
        }
        val destination = arguments?.getString("destination")
        val startDate = arguments?.getString("startDate")

        val length = arguments?.getString("length")
        val limits = arguments?.getString("limits")
       val weight=arguments?.getString("weight")
        val origin = arguments?.getString("origin")
        val phone = arguments?.getString("phone")
        val endDate = arguments?.getString("endDate")


        val Type = arguments?.getString("Type")

        // Find the views and set values
        view.findViewById<TextView>(R.id.source).text = origin
        view.findViewById<TextView>(R.id.destination).text = destination

        view.findViewById<TextView>(R.id.Pickupdate).text = startDate
        view.findViewById<TextView>(R.id.length).text = length
        view.findViewById<TextView>(R.id.truck_type).text = Type
        view.findViewById<TextView>(R.id.weight).text=weight
        view.findViewById<TextView>(R.id.Dropoffdate).text=endDate

        view.findViewById<TextView>(R.id.load_type).text=limits
        view.findViewById<TextView>(R.id.Phone).text=phone

        return view
    }
    private fun bookTruck() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("shippers")
            .child(userId)
            .child("bookedTrucks")

        val loadId = databaseRef.push().key // Generate unique key for booking

        val truckID = arguments?.getString("truckID") ?: return // Get the truckID from arguments

        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

        // Fetch user's phone number directly from the user ID in the "shippers" node
        val userRef = FirebaseDatabase.getInstance().getReference("shippers").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            val userPhone = snapshot.child("phone").getValue(String::class.java) ?: ""  // Fetch phone number from child node

            val load = truckdata(
                destination = arguments?.getString("destination") ?: "",
                startDate = arguments?.getString("dropoffDate") ?: "",
                endDate = arguments?.getString("dropoffTime") ?: "",
                length = arguments?.getString("length") ?: "",
                limits = arguments?.getString("limits") ?: "",
                weight = arguments?.getString("weight") ?: "",
                origin = arguments?.getString("origin") ?: "",
                phone = userPhone, // Store user's phone number
                type = arguments?.getString("truckType") ?: "",
                truckID = truckID, // Add truckID to load data
                email = userEmail // Store user's email
            )

            if (loadId != null) {
                // Step 1: Find the truck using its truckID in the 'users' node
                val truckRef = FirebaseDatabase.getInstance().getReference("users")
                databaseRef.child(loadId).setValue(load) // ✅ Store only inside bookedTrucks
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener {

                    }
                truckRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (userSnap in snapshot.children) {
                            // Iterate through the children of the 'users' node (to get truck details)
                            val trucks = userSnap.child("trucks")
                            for (truckSnap in trucks.children) {
                                val truckData = truckSnap.getValue(truckdata::class.java)
                                if (truckData != null && truckSnap.key == truckID) {
                                    // Step 2: If truck ID matches, store the entire truck data under 'bookedtruckreq' node
                                    val bookedTruckRef = FirebaseDatabase.getInstance()
                                        .getReference("users")
                                        .child(userSnap.key!!) // Using the user ID key
                                        .child("bookedtruckreq")
                                        .push() // Generate a unique key under bookedtruckreq

                                    // Add email and phone to truckData before storing it in bookedtruckreq
                                    val bookedTruckData = truckData.copy(email = userEmail, phone = userPhone)

                                    // Store the truck details along with the user's email and phone under "bookedtruckreq"
                                    bookedTruckRef.setValue(bookedTruckData)
                                        .addOnSuccessListener {
                                            Toast.makeText(requireContext(), "Truck booked successfully!", Toast.LENGTH_SHORT).show()
                                            navigateToMyLoads()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(requireContext(), "Failed to book truck", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error if database read is cancelled
                    }
                })
            }
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to retrieve phone number", Toast.LENGTH_SHORT).show()
            }
    }







    private fun navigateToMyLoads() {
        val bookedTrucks = BookedTrucks()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, bookedTrucks)
            .addToBackStack(null)
            .commit()

        // ✅ Update BottomNavigationView selection
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.btnview)
        bottomNav.selectedItemId = R.id.BookedTrucks // Set the correct menu item ID
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
}