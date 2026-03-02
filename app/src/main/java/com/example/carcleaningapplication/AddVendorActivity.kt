package com.example.carcleaningapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carcleaningapplication.databinding.ActivityAddVendorBinding

class AddVendorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddVendorBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: AdminVendorAdapter
    private var vendorList: List<VendorDataClass> = emptyList()

    private val refreshLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadVendors()
                Toast.makeText(this, "Vendor list updated.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVendorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setSupportActionBar(binding.toolbarAddVendor)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Manage Vendors"

        setupRecyclerView()
        loadVendors()
        setupListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupRecyclerView() {
        adapter = AdminVendorAdapter(
            vendorList,
            onDeleteClicked = { vendorId -> deleteVendor(vendorId) },
            onEditClicked = { vendor -> launchEditVendor(vendor) }
        )

        binding.recyclerViewVendors.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewVendors.adapter = adapter
    }

    private fun loadVendors() {
        vendorList = dbHelper.readAllVendors()
        adapter.updateData(vendorList)
        binding.tvVendorListHeader.text = "All Registered Vendors (${vendorList.size})"
    }

    private fun setupListeners() {
        binding.btnAddVendor.setOnClickListener {
            launchEditVendor(null)
        }
    }

    private fun launchEditVendor(vendor: VendorDataClass?) {
        val intent = Intent(this, EditVendorActivity::class.java).apply {
            if (vendor != null) {
                putExtra("EXTRA_VENDOR_ID", vendor.vendorId)
                putExtra("EXTRA_VENDOR_NAME", vendor.name)
                putExtra("EXTRA_VENDOR_EMAIL", vendor.email)
                putExtra("EXTRA_VENDOR_RATING", vendor.rating)
                putExtra("EXTRA_VENDOR_AVAILABILITY", vendor.availability)
            }
        }
        refreshLauncher.launch(intent)
    }

    private fun deleteVendor(vendorId: String) {
        val rowsAffected = dbHelper.deleteVendor(vendorId)

        if (rowsAffected > 0) {
            Toast.makeText(this, "Vendor deleted successfully.", Toast.LENGTH_SHORT).show()
            loadVendors()
        } else {
            Toast.makeText(this, "Failed: Vendor has associated services. Delete services first.", Toast.LENGTH_LONG).show()
        }
    }
}