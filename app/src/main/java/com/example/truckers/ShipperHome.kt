package com.example.truckers

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class ShipperHome : AppCompatActivity() {
    private lateinit var bnView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shipper_home)
        bnView = findViewById(R.id.btnview)

        bnView.setOnNavigationItemSelectedListener { item: MenuItem ->
            val id = item.itemId
            when (id) {
                R.id.BookedTrucks-> loadFrg(BookedTrucks(), true)
                R.id.PickUpRequests -> loadFrg(PickupReq(), false)
                R.id.postload -> loadFrg(postloads(), false)
                R.id.SearchTruck -> loadFrg(searchtruck(), false)
                else -> loadFrg(ShipperProfile(), false)
            }
            true
        }

        bnView.selectedItemId = R.id.BookedTrucks
    }
    private fun loadFrg(fragment: Fragment, flag: Boolean) {
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()

        // Remove any existing fragment in the container
        val existingFragment = fm.findFragmentById(R.id.container)
        if (existingFragment != null) {
            ft.remove(existingFragment)
        }

        // Replace the fragment
        ft.replace(R.id.container, fragment)

        // Add the transaction to the back stack
        if (flag) {
            ft.addToBackStack(null) // Adds the fragment to the back stack
        }

        ft.commit()
    }
    }
