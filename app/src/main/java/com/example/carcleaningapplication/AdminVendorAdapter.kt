package com.example.carcleaningapplication

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class AdminVendorAdapter(
    private var vendorList: List<VendorDataClass>,
    private val onDeleteClicked: (String) -> Unit,
    private val onEditClicked: (VendorDataClass) -> Unit
) : RecyclerView.Adapter<AdminVendorAdapter.VendorViewHolder>() {

    inner class VendorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvVendorName: TextView = itemView.findViewById(R.id.tvVendorName)
        val tvVendorDetails: TextView = itemView.findViewById(R.id.tvVendorDetails)
        val btnEditVendor: Button = itemView.findViewById(R.id.btnEditVendor)
        val btnDeleteVendor: Button = itemView.findViewById(R.id.btnDeleteVendor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_vendor_admin, parent, false)
        return VendorViewHolder(view)
    }

    override fun onBindViewHolder(holder: VendorViewHolder, position: Int) {
        val currentVendor = vendorList[position]
        val context = holder.itemView.context

        holder.tvVendorName.text = currentVendor.name

        holder.tvVendorDetails.text =
            "Rating: ${currentVendor.rating} | Email: ${currentVendor.email ?: "N/A"} | Available: ${if (currentVendor.availability) "Yes" else "No"}"

        holder.btnEditVendor.setOnClickListener {
            onEditClicked(currentVendor)
        }

        holder.btnDeleteVendor.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirm Vendor Deletion")
                .setMessage("Are you sure you want to permanently delete vendor ${currentVendor.name}? This operation will fail if the vendor has associated services.")
                .setPositiveButton("DELETE") { _, _ ->
                    onDeleteClicked(currentVendor.vendorId)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int {
        return vendorList.size
    }

    fun updateData(newList: List<VendorDataClass>) {
        vendorList = newList
        notifyDataSetChanged()
    }
}