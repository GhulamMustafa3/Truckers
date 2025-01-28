package com.example.truckers

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction

class truckdetails : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var register: TextView
    private lateinit var vphoto: TextView
    private lateinit var cinfo: TextView
    private lateinit var equipmentLimit: TextView
    private lateinit var routes: TextView
    private lateinit var btnDone: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_truckdetails, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        progressBar = view.findViewById(R.id.progress_bar)
        register = view.findViewById(R.id.Register_info)
        vphoto = view.findViewById(R.id.vehicle_photo)
        cinfo = view.findViewById(R.id.Certificate_info)
        equipmentLimit = view.findViewById(R.id.Equipmentlimit)
        routes = view.findViewById(R.id.truckroutes)
        btnDone = view.findViewById(R.id.btn_done)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("DriverRegistrationPrefs", MODE_PRIVATE)

        // Replace Intent with FragmentTransaction

        register.setOnClickListener {
            navigateToFragment(registration())
        }
        vphoto.setOnClickListener {
            navigateToFragment(vehicleimage())
        }
        cinfo.setOnClickListener {
            navigateToFragment(vehicleregisterpic())
        }
        equipmentLimit.setOnClickListener {
            navigateToFragment(equipmentlimits())
        }
        routes.setOnClickListener {
            navigateToFragment(truckroutes())
        }
        btnDone.setOnClickListener {
            // Show the progress bar when checking fields
            progressBar.visibility = View.VISIBLE

            if (areFieldsCompleted()) {
                // Hide the progress bar and navigate to the next fragment
                progressBar.visibility = View.GONE

                val editor = sharedPreferences.edit()
                editor.putBoolean("isVehicleInfoComplete", true)
                editor.apply()

                navigateToFragment(mytruck())
            } else {
                // Hide the progress bar and show an error message
                progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Please complete all required fields before proceeding.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun areFieldsCompleted(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
        val registrationInfoCompleted = sharedPreferences.getBoolean("registrationInfoCompleted", false)
        val vehiclePhotoCompleted = sharedPreferences.getBoolean("vehiclePhotoCompleted", false)
        val certificateInfoCompleted = sharedPreferences.getBoolean("certificateInfoCompleted", false)
        val equipmentDetailsComplete = sharedPreferences.getBoolean("equipmentDetailsComplete", false)
        val routesDetailsComplete = sharedPreferences.getBoolean("routesdetailsComplete", false)

        return registrationInfoCompleted && vehiclePhotoCompleted && certificateInfoCompleted &&
                equipmentDetailsComplete && routesDetailsComplete
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null) // Adds the transaction to the back stack
        transaction.commit()
    }
}
