package com.example.truckers

data class loaddata(
    val origin: String ?= null,
    val destination: String ?= null,
    val weight: String ?= null,
    val length: String ?= null,
    val limits: String ?= null,
    val pickupDate:String?=null,
    val price:String?=null,
    val dropoffDate: String?= null,  // Add this
    val dropoffTime: String?= null,    // Add this
    val material: String?= null,    // Add this
    val phone: String?= null,       // Add this
    val pickupTime: String?= null,  // Add this
    val truckType: String?= null,
    var loadId:String?=null
)
