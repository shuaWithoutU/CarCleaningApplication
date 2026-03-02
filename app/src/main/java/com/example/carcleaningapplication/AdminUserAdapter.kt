package com.example.carcleaningapplication

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class AdminUserAdapter(
    private var userList: List<UserDataClass>,
    private val onDeleteClicked: (String) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val btnDeleteUser: Button = itemView.findViewById(R.id.btnDeleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_user_admin, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        val context = holder.itemView.context

        holder.tvUserEmail.text = "${currentUser.email} (${currentUser.role.toLowerCase(Locale.ROOT)})"
        holder.tvUserName.text = "Username: ${currentUser.username} | Phone: ${currentUser.phoneNumber}"

        holder.btnDeleteUser.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to permanently delete user ${currentUser.username} and ALL their appointments/vehicles?")
                .setPositiveButton("DELETE") { dialog, which ->
                    onDeleteClicked(currentUser.userId) // Execute the callback passed from the Activity
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateData(newList: List<UserDataClass>) {
        userList = newList
        notifyDataSetChanged()
    }
}