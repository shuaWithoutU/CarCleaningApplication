package com.example.carcleaningapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VehicleAdapter(
    private var vehicleList: List<VehicleDataClass>,
    private val onDeleteClickListener: (VehicleDataClass) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMakeModel: TextView = itemView.findViewById(R.id.tvVehicleMakeModel)
        val tvYear: TextView = itemView.findViewById(R.id.tvVehicleYear)
        val btnDelete: Button = itemView.findViewById(R.id.btnVehicleDelete)

        init {
            btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClickListener(vehicleList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val currentVehicle = vehicleList[position]

        holder.tvMakeModel.text = "${currentVehicle.make} ${currentVehicle.model}"
        holder.tvYear.text = "Year: ${currentVehicle.year}"
    }

    override fun getItemCount() = vehicleList.size

    fun updateVehicleList(newList: List<VehicleDataClass>) {
        vehicleList = newList
        notifyDataSetChanged()
    }
}