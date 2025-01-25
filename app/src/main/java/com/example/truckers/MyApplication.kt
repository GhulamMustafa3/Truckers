package com.example.truckers

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager

class MyApplication : Application() {
    lateinit var cloudinary: Cloudinary

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Configure Cloudinary
        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = "dwumtbv5g" // Replace with your Cloudinary cloud name
        config["api_key"] = "729242625281598" // Replace with your API key
        config["api_secret"] = "PweJc4txJUPEbg70TR7fYqwyRJw" // Replace with your API secret

        // Initialize MediaManager for Cloudinary
        MediaManager.init(this, config)

        // Initialize Cloudinary instance
        cloudinary = Cloudinary(config)
    }
}
