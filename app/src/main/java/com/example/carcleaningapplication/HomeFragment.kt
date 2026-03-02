package com.example.carcleaningapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter

    private var serviceList: List<ServiceDataClass> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        recyclerView = view.findViewById(R.id.recyclerViewServices)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadServicesFromDatabase()

        serviceAdapter = ServiceAdapter(serviceList)
        recyclerView.adapter = serviceAdapter
    }

    private fun loadServicesFromDatabase() {
        val services = dbHelper.readAllServices()

        if (services.isNotEmpty()) {
            serviceList = services

        } else {
            Toast.makeText(requireContext(), "No services found. Database may not be seeded.", Toast.LENGTH_LONG).show()
            serviceList = emptyList()
        }
    }
}