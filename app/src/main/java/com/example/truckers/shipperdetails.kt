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

class shipperdetails : Fragment() {
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var button: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shipperdetails, container, false)

        emailTextView = view.findViewById(R.id.email)
        phoneTextView = view.findViewById(R.id.Phone)
        button = view.findViewById(R.id.call)

        val bundle = arguments
        val shipperEmail = bundle?.getString("shipper_email")
        val shipperPhone = bundle?.getString("shipper_phone")

        // Display the email and phone in the TextViews
        emailTextView.text = shipperEmail ?: "Email not available"
        phoneTextView.text = shipperPhone ?: "Phone not available"

        // Call Button Click
        button.setOnClickListener { makePhoneCall() }

        return view
    }

    private fun makePhoneCall() {
        val phoneNumber = phoneTextView.text.toString().trim()

        if (phoneNumber.isNotEmpty() && phoneNumber != "Phone not available") {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")

            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    REQUEST_CALL_PHONE
                )
            }
        } else {
            Toast.makeText(requireContext(), "Phone number not available", Toast.LENGTH_SHORT).show()
        }
    }

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
