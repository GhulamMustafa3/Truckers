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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class loadcompletedetails : Fragment() {
    private lateinit var phoneTextView: TextView
    private lateinit var callButton: Button
    private lateinit var bookNowButton:Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loadcompletedetails, container, false)
        phoneTextView = view.findViewById(R.id.Phone) // Get phone number TextView
        callButton = view.findViewById(R.id.call)     // Get Call button
        bookNowButton=view.findViewById(R.id.book)
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
        val bookid=databaseRef.push().key

        val bookedLoadsRef = userRef.child("bookedLoads")
        val loadId = bookedLoadsRef.push().key // Generate unique key for the booked load
        val loadIds = arguments?.getString("loadId") ?: return
        val load = loaddata(
            destination = arguments?.getString("destination") ?: "",
            dropoffDate = arguments?.getString("dropoffDate") ?: "",
            dropoffTime = arguments?.getString("dropoffTime") ?: "",
            length = arguments?.getString("length") ?: "",
            limits = arguments?.getString("limits") ?: "",
            material = arguments?.getString("material") ?: "",
            origin = arguments?.getString("origin") ?: "",
            phone = arguments?.getString("phone") ?: "",
            pickupDate = arguments?.getString("pickupDate") ?: "",
            pickupTime = arguments?.getString("pickupTime") ?: "",
            price = arguments?.getString("price") ?: "",
            truckType = arguments?.getString("truckType") ?: "",
            loadId = loadIds

        )

        if (loadId != null) {
            bookedLoadsRef.child(loadId).setValue(load)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Load booked successfully!", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to book load", Toast.LENGTH_SHORT).show()
                }
        }
        if(bookid!=null){
            val loadref=FirebaseDatabase.getInstance().getReference("shippers")
            loadref.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(usersnap in snapshot.children){
                        val userId = usersnap.key
                        val loads=usersnap.child("loaddetails")
                        for(loadsnap in loads.children){
                            val loaddata=loadsnap.getValue(loaddata::class.java)
                            if(loaddata!=null &&loadsnap.key==loadIds){
                                val loaddetails=FirebaseDatabase.getInstance().getReference("shippers").child(userId!!)
                                    .child("bookedloadsreq").push()
                                loaddetails.setValue(loaddata).addOnSuccessListener { navigateToMyLoads() }
                                    .addOnFailureListener{}
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
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

}
