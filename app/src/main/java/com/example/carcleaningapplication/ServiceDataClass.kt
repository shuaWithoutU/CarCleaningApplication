package com.example.carcleaningapplication

data class ServiceDataClass(
    val serviceId: String,
    val vendorId: String,
    val vendorName: String,
    val packageType: String,
    val fare: Double,
    val availableDays: String,
    val vendorRating: Double
)