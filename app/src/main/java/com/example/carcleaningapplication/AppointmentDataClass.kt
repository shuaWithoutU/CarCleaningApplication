package com.example.carcleaningapplication

data class AppointmentDataClass(
    val appointmentId: String,
    val serviceId: String,
    val userId: String,
    val vehicleId: String,
    val appointmentTime: Long,
    val totalPrice: Double,
    val status: String,
    val vendorName: String,
    val packageType: String,
    val vehicleMakeModel: String
)