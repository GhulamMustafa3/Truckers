package com.example.truckers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Selection : AppCompatActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_selection)
        val individual: Button =findViewById(R.id.individual)
        val business:Button=findViewById(R.id.business)
        val userType = intent.getStringExtra("userType")

            if (userType == "Driver") {
                individual.setOnClickListener{
                    val intent= Intent(this,IndividualDriverRegis::class.java)
                    startActivity(intent)
                }

            } else if (userType == "Shipper") {
                individual.setOnClickListener{
                    val intent= Intent(this,ShipperIndividual::class.java)
                    startActivity(intent)
                }



        }

            if (userType == "Driver") {
                business.setOnClickListener{
                    val intent= Intent(this,businessregis::class.java)
                    intent.putExtra("userType", "Driver")
                    startActivity(intent)
                }

            } else if (userType == "Shipper") {
                business.setOnClickListener{
                    val intent= Intent(this,shipperbusinessreg::class.java)
                    intent.putExtra("userType", "Shipper")
                    startActivity(intent)
                }

            }
        }
    }
