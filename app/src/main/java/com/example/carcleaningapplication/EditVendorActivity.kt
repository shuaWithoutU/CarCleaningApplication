package com.example.carcleaningapplication

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carcleaningapplication.databinding.ActivityEditVendorBinding

class EditVendorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditVendorBinding
    private lateinit var dbHelper: DatabaseHelper

    private var vendorId: String? = null
    private var vendorName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditVendorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setSupportActionBar(binding.toolbarEditVendor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getIntentData()

        binding.btnSaveVendor.setOnClickListener {
            validateAndSaveVendor()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun getIntentData() {
        vendorName = intent.getStringExtra("EXTRA_VENDOR_NAME")
        vendorId = intent.getStringExtra("EXTRA_VENDOR_ID")


        if (vendorId != null) {
            supportActionBar?.title = "Edit Vendor: ${vendorName ?: "Unknown"}"
            binding.btnSaveVendor.text = "Update Vendor"

            binding.editTextVendorName.setText(intent.getStringExtra("EXTRA_VENDOR_NAME"))
            binding.editTextVendorEmail.setText(intent.getStringExtra("EXTRA_VENDOR_EMAIL"))
            binding.editTextVendorRating.setText(intent.getDoubleExtra("EXTRA_VENDOR_RATING", 0.0).toString())
            binding.switchAvailability.isChecked = intent.getIntExtra("EXTRA_VENDOR_AVAILABILITY", 1) == 1

        } else {
            supportActionBar?.title = "Add New Vendor"
            binding.btnSaveVendor.text = "Add Vendor"
            binding.switchAvailability.isChecked = true
        }
    }

    private fun validateAndSaveVendor() {
        val name = binding.editTextVendorName.text.toString().trim()
        val email = binding.editTextVendorEmail.text.toString().trim()
        val ratingString = binding.editTextVendorRating.text.toString().trim()
        val isAvailable = binding.switchAvailability.isChecked

        if (name.isEmpty() || ratingString.isEmpty()) {
            Toast.makeText(this, "Name and Rating are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val rating = ratingString.toDoubleOrNull()
        if (rating == null || rating < 1.0 || rating > 5.0) {
            Toast.makeText(this, "Rating must be between 1.0 and 5.0.", Toast.LENGTH_SHORT).show()
            return
        }

        val emailToSend = if (email.isEmpty()) null else email

        var success = false

        if (vendorId == null) {
            val newId = dbHelper.insertVendor(name, emailToSend, rating, isAvailable)
            if (newId != null) {
                Toast.makeText(this, "Vendor added successfully!", Toast.LENGTH_SHORT).show()
                success = true
            }
        } else {
            val vendorToUpdate = VendorDataClass(
                vendorId = vendorId!!,
                name = name,
                email = emailToSend ?: "",
                rating = rating,
                availability = isAvailable
            )
            val rows = dbHelper.updateVendor(vendorToUpdate)
            if (rows > 0) {
                Toast.makeText(this, "Vendor updated successfully!", Toast.LENGTH_SHORT).show()
                success = true
            }
        }

        if (success) {
            setResult(Activity.RESULT_OK)
            finish()
        } else if (vendorId != null) {
            Toast.makeText(this, "Update failed. Try again.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Add failed. Try again.", Toast.LENGTH_SHORT).show()
        }
    }
}