package com.example.carcleaningapplication

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class AdminServiceAdapter(
    private var serviceList: List<ServiceDataClass>,
    private val onDeleteClicked: (String) -> Unit,
    private val onEditClicked: (ServiceDataClass) -> Unit
) : RecyclerView.Adapter<AdminServiceAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPackageType: TextView = itemView.findViewById(R.id.tvPackageType)
        val tvServiceDetails: TextView = itemView.findViewById(R.id.tvServiceDetails)
        val btnEditService: Button = itemView.findViewById(R.id.btnEditService)
        val btnDeleteService: Button = itemView.findViewById(R.id.btnDeleteService)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_service_admin, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val currentService = serviceList[position]
        val context = holder.itemView.context

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MY")).apply {
            maximumFractionDigits = 2
        }
        val formattedFare = currencyFormat.format(currentService.fare)

        holder.tvPackageType.text = currentService.packageType
        holder.tvServiceDetails.text =
            "Vendor: ${currentService.vendorName} | Fare: $formattedFare | Days: ${currentService.availableDays}"

        holder.btnEditService.setOnClickListener {
            onEditClicked(currentService)
        }

        holder.btnDeleteService.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirm Service Deletion")
                .setMessage("Are you sure you want to permanently delete the service '${currentService.packageType}'? This operation will fail if the service has existing customer appointments.")
                .setPositiveButton("DELETE") { _, _ ->
                    onDeleteClicked(currentService.serviceId)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    fun updateData(newList: List<ServiceDataClass>) {
        serviceList = newList
        notifyDataSetChanged()
    }
}