package com.example.carcleaningapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carcleaningapplication.databinding.ActivityAddServiceBinding

class AddServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddServiceBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: AdminServiceAdapter
    private var serviceList: List<ServiceDataClass> = emptyList()

    private val refreshLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadServices()
                Toast.makeText(this, "Service list updated.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setSupportActionBar(binding.toolbarAddService)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Manage Services"

        setupRecyclerView()
        loadServices()
        setupListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupRecyclerView() {
        adapter = AdminServiceAdapter(
            serviceList,
            onDeleteClicked = { serviceId -> deleteService(serviceId) },
            onEditClicked = { service -> launchEditService(service) }
        )

        binding.recyclerViewServices.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewServices.adapter = adapter
    }

    private fun loadServices() {
        serviceList = dbHelper.readAllServices()
        adapter.updateData(serviceList)
        binding.tvServiceListHeader.text = "All Registered Services (${serviceList.size})"
    }

    private fun setupListeners() {
        binding.btnAddService.setOnClickListener {
            launchEditService(null)
        }
    }

    private fun launchEditService(service: ServiceDataClass?) {
        val intent = Intent(this, EditServiceActivity::class.java).apply {
            if (service != null) {
                putExtra("EXTRA_SERVICE_ID", service.serviceId)
                putExtra("EXTRA_VENDOR_ID", service.vendorId) // Needed to link service
                putExtra("EXTRA_PACKAGE_TYPE", service.packageType)
                putExtra("EXTRA_FARE", service.fare)
                putExtra("EXTRA_AVAILABLE_DAYS", service.availableDays)
           }
        }
        refreshLauncher.launch(intent)
    }

    private fun deleteService(serviceId: String) {
        val rowsAffected = dbHelper.deleteService(serviceId)

        if (rowsAffected > 0) {
            Toast.makeText(this, "Service deleted successfully.", Toast.LENGTH_SHORT).show()
            loadServices()
        } else {
            Toast.makeText(this, "Failed: Service has existing customer appointments. Cannot delete.", Toast.LENGTH_LONG).show()
        }
    }
}