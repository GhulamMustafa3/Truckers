package com.example.truckers

data class truckdata(

    val origin: String ?= null,
    val destination: String ?= null,
    val weight: String ?= null,
    val length: String ?= null,
    val limits: String ?= null,
    val type:String?= null,
    val endDate:String?= null,
    val startDate:String?= null,
    var phone:String?=null,
    var truckID: String? = null,
    var email:String?=null
)
