package com.example.carcleaningapplication

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carcleaningapplication.databinding.ActivityEditServiceBinding
import java.util.Locale

class EditServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditServiceBinding
    private lateinit var dbHelper: DatabaseHelper

    private var serviceId: String? = null
    private var vendorList: List<VendorDataClass> = emptyList()
    private var selectedVendorId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setSupportActionBar(binding.toolbarEditService)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadVendorsAndSetupSpinner()

        getIntentData()

        binding.btnSaveService.setOnClickListener {
            validateAndSaveService()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadVendorsAndSetupSpinner() {
        vendorList = dbHelper.readAllVendors()

        if (vendorList.isEmpty()) {
            Toast.makeText(this, "ERROR: No vendors found. Add a vendor first.", Toast.LENGTH_LONG).show()
            binding.btnSaveService.isEnabled = false
            return
        }

        val vendorOptions = vendorList.map { "${it.name} (Rating: ${it.rating})" }.toMutableList()
        vendorOptions.add(0, "--- Select a Vendor ---")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vendorOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerVendor.adapter = adapter

        binding.spinnerVendor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    // Position 0 is the placeholder
                    selectedVendorId = vendorList[position - 1].vendorId
                } else {
                    selectedVendorId = null
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedVendorId = null
            }
        }
    }

    private fun getIntentData() {
        serviceId = intent.getStringExtra("EXTRA_SERVICE_ID")
        val packageName = intent.getStringExtra("EXTRA_PACKAGE_TYPE")

        if (serviceId != null) {

            supportActionBar?.title = "Edit Service: ${packageName ?: serviceId!!.take(4) + "..."}"
            binding.btnSaveService.text = "Update Service"

            val vendorIdToSelect = intent.getStringExtra("EXTRA_VENDOR_ID")

            binding.editTextPackageType.setText(packageName) // Already retrieved above
            binding.editTextBaseFare.setText(intent.getDoubleExtra("EXTRA_FARE", 0.0).toString())
            binding.editTextAvailableDays.setText(intent.getStringExtra("EXTRA_AVAILABLE_DAYS"))

            val selectedIndex = vendorList.indexOfFirst { it.vendorId == vendorIdToSelect }
            if (selectedIndex >= 0) {
                binding.spinnerVendor.setSelection(selectedIndex + 1)
            }

        } else {
            supportActionBar?.title = "Add New Service"
            binding.btnSaveService.text = "Add Service"
        }
    }

    private fun validateAndSaveService() {
        val packageType = binding.editTextPackageType.text.toString().trim()
        val fareString = binding.editTextBaseFare.text.toString().trim()
        val availableDays = binding.editTextAvailableDays.text.toString().trim()

        if (selectedVendorId == null) {
            Toast.makeText(this, "Please select a vendor.", Toast.LENGTH_SHORT).show()
            return
        }
        if (packageType.isEmpty() || fareString.isEmpty() || availableDays.isEmpty()) {
            Toast.makeText(this, "All service fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val fare = fareString.toDoubleOrNull()
        if (fare == null || fare <= 0) {
            Toast.makeText(this, "Fare must be a positive number.", Toast.LENGTH_SHORT).show()
            return
        }

        var success = false

        if (serviceId == null) {
            val newId = dbHelper.insertService(
                vendorId = selectedVendorId!!,
                packageType = packageType,
                fare = fare,
                availableDays = availableDays
            )
            if (newId != null) {
                Toast.makeText(this, "Service added successfully!", Toast.LENGTH_SHORT).show()
                success = true
            }
        } else {

            val currentVendor = vendorList.firstOrNull { it.vendorId == selectedVendorId }

            val serviceToUpdate = ServiceDataClass(
                serviceId = serviceId!!,
                vendorId = selectedVendorId!!,
                vendorName = currentVendor?.name ?: "",
                packageType = packageType,
                fare = fare,
                availableDays = availableDays,
                vendorRating = currentVendor?.rating ?: 0.0
            )
            val rows = dbHelper.updateService(serviceToUpdate)
            if (rows > 0) {
                Toast.makeText(this, "Service updated successfully!", Toast.LENGTH_SHORT).show()
                success = true
            }
        }

        if (success) {
            setResult(Activity.RESULT_OK)
            finish()
        } else if (serviceId != null) {
            Toast.makeText(this, "Update failed. Try again.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Add failed. Try again.", Toast.LENGTH_SHORT).show()
        }
    }
}