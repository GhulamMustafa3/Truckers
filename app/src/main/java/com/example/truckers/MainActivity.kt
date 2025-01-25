package com.example.truckers


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val driverLayout: LinearLayout = findViewById(R.id.first_card)
        val shipperLayout: LinearLayout = findViewById(R.id.second_card)

        val driverTextView: TextView = findViewById(R.id.DriverRegistration)
        val shipperTextView: TextView = findViewById(R.id.ShipperRegistration)


        driverLayout.setOnClickListener {
            val intent = Intent(this, Selection::class.java)
            intent.putExtra("userType", "Driver")
            startActivity(intent)
        }

        shipperLayout.setOnClickListener {
            val intent = Intent(this, Selection::class.java)
            intent.putExtra("userType", "Shipper")
            startActivity(intent)
        }

    }
}