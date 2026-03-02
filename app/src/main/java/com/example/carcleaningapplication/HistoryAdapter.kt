package com.example.carcleaningapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carcleaningapplication.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val dataList: List<AppointmentDataClass>,
    private val onCancelClicked: (AppointmentDataClass) -> Unit
) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPackageType: TextView = itemView.findViewById(R.id.text_package_type)
        val tvVendorName: TextView = itemView.findViewById(R.id.text_vendor_name)
        val tvDateTime: TextView = itemView.findViewById(R.id.text_booking_date)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.text_total_price)
        val tvStatus: TextView = itemView.findViewById(R.id.text_status)
        val tvVehicleInfo: TextView = itemView.findViewById(R.id.text_vehicle_info)
        val btnCancel: Button = itemView.findViewById(R.id.btnCancelAppointment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_list_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = dataList[position]
        val context = holder.itemView.context

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateString = dateFormat.format(Date(currentItem.appointmentTime))

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MY")).apply {
            maximumFractionDigits = 2
        }
        val formattedPrice = currencyFormat.format(currentItem.totalPrice)

        val status = currentItem.status.toUpperCase(Locale.ROOT)

        val statusColorRes = when (status) {
            "CONFIRMED" -> android.R.color.holo_green_dark
            "PENDING" -> android.R.color.holo_orange_dark
            "COMPLETED" -> android.R.color.holo_blue_dark
            "CANCELLED" -> android.R.color.holo_red_dark
            else -> android.R.color.darker_gray
        }

        holder.tvVendorName.text = currentItem.vendorName
        holder.tvPackageType.text = "Package: ${currentItem.packageType}"
        holder.tvDateTime.text = "Booked: $dateString"
        holder.tvTotalPrice.text = formattedPrice
        holder.tvStatus.text = status
        holder.tvVehicleInfo.text = "Vehicle: ${currentItem.vehicleMakeModel}"
        holder.tvStatus.setTextColor(context.getColor(statusColorRes))

        if (status == "CONFIRMED") {
            holder.btnCancel.visibility = View.VISIBLE
            holder.btnCancel.setOnClickListener {
                onCancelClicked(currentItem)
            }
        } else {
            holder.btnCancel.visibility = View.GONE
            holder.btnCancel.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newList: List<AppointmentDataClass>) {
        (this.dataList as? MutableList)?.clear()
        (this.dataList as? MutableList)?.addAll(newList)
        notifyDataSetChanged()
    }
}