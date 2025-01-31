package com.example.truckers

data class loaddata(
    val origin: String ?= null,
    val destination: String ?= null,
    val weight: String ?= null,
    val length: String ?= null,
    val limits: String ?= null,
    val pickupDate:String?=null,
    val price:String?=null
)
