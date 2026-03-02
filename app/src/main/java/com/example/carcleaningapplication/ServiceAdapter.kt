package com.example.carcleaningapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carcleaningapplication.R
import java.util.Locale

class ServiceAdapter(private val serviceList: List<ServiceDataClass>) :
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    companion object {
        const val EXTRA_SERVICE_ID = "SERVICE_ID"
        const val EXTRA_VENDOR_NAME = "VENDOR_NAME"
        const val EXTRA_PACKAGE_TYPE = "PACKAGE_TYPE"
        const val EXTRA_BASE_FARE = "BASE_FARE"
        const val EXTRA_AVAILABLE_DAYS = "AVAILABLE_DAYS"
    }

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vendorName: TextView = itemView.findViewById(R.id.serviceVendorName)
        val packageType: TextView = itemView.findViewById(R.id.servicePackageType)
        val price: TextView = itemView.findViewById(R.id.servicePrice)
        val availableDays: TextView = itemView.findViewById(R.id.serviceAvailableDays)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val service = serviceList[position]

                    val intent = Intent(itemView.context, BookingDetailsActivity::class.java).apply {
                        putExtra(EXTRA_SERVICE_ID, service.serviceId)

                        putExtra(EXTRA_VENDOR_NAME, service.vendorName)
                        putExtra(EXTRA_PACKAGE_TYPE, service.packageType)

                        putExtra(EXTRA_BASE_FARE, service.fare)
                        putExtra(EXTRA_AVAILABLE_DAYS, service.availableDays)
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val currentService = serviceList[position]

        val formattedFare = String.format(Locale.getDefault(), "RM %.2f", currentService.fare)

        holder.vendorName.text = "Vendor: ${currentService.vendorName} (${currentService.vendorRating} ⭐)"
        holder.packageType.text = "Type: ${currentService.packageType}"
        holder.price.text = formattedFare
        holder.availableDays.text = "Availability: ${currentService.availableDays}"
    }

    override fun getItemCount() = serviceList.size
}