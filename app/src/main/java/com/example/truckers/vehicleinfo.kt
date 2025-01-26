package com.example.truckers

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class vehicleinfo : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehicleinfo)
        sharedPreferences = getSharedPreferences("DriverRegistrationPrefs", MODE_PRIVATE)
        val register: TextView = findViewById(R.id.Register_info)
        val vphoto: TextView = findViewById(R.id.vehicle_photo)
        val cinfo: TextView = findViewById(R.id.Certificate_info)
        val driverLisence: TextView = findViewById(R.id.DriverLiscence)
        val equipmentlimit: TextView = findViewById(R.id.Equipmentlimit)
        val rotes: TextView = findViewById(R.id.truckroutes)
        val btndone: Button = findViewById(R.id.btn_done)
        progressBar = findViewById(R.id.progress_bar)

        equipmentlimit.setOnClickListener {
            val intent = Intent(this, EquipLimits::class.java)
            startActivity(intent)
        }
        rotes.setOnClickListener {
            val intent = Intent(this, TrucRoutes::class.java)
            startActivity(intent)
        }
        register.setOnClickListener {
            val intent = Intent(this, registrationinfo::class.java)
            startActivity(intent)
        }

        vphoto.setOnClickListener {
            val intent = Intent(this, vehiclephoto::class.java)
            startActivity(intent)
        }

        cinfo.setOnClickListener {
            val intent = Intent(this, VehicleCertificate::class.java)
            startActivity(intent)
        }

        driverLisence.setOnClickListener {
            val intent = Intent(this, DriverLisence::class.java)
            startActivity(intent)
        }

        btndone.setOnClickListener {
            // Show the progress bar when checking fields
            progressBar.visibility = View.VISIBLE

            if (areFieldsCompleted()) {
                // Hide the progress bar and navigate to the next activity
                progressBar.visibility = View.GONE

                val editor = sharedPreferences.edit()
                editor.putBoolean("isVehicleInfoComplete", true)
                editor.apply()

                val intent = Intent(this, IndividualDriverRegis::class.java)
                startActivity(intent)
            } else {
                // Hide the progress bar and show an error message
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Please complete all required fields before proceeding.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun areFieldsCompleted(): Boolean {
        val sharedPreferences = getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
        val registrationInfoCompleted = sharedPreferences.getBoolean("registrationInfoCompleted", false)
        val vehiclePhotoCompleted = sharedPreferences.getBoolean("vehiclePhotoCompleted", false)
        val certificateInfoCompleted = sharedPreferences.getBoolean("certificateInfoCompleted", false)
        val driverLicenseCompleted = sharedPreferences.getBoolean("driverLicenseCompleted", false)
        val equipmentDetailsCompleted = sharedPreferences.getBoolean("equipmentDetailsCompleted", false)
        val routesdetailsCompleted = sharedPreferences.getBoolean("routesdetailsCompleted", false)

        return registrationInfoCompleted && vehiclePhotoCompleted && certificateInfoCompleted &&
                driverLicenseCompleted && equipmentDetailsCompleted && routesdetailsCompleted
    }
}
