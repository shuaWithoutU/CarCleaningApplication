package com.example.carcleaningapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ProfileFragment : Fragment() {

    companion object {
        const val EXTRA_USER_ID = "com.example.carcleaningapplication.USER_ID"
    }

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: String? = null

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvUserId: TextView
    private lateinit var tvDateRegistered: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button

    private lateinit var recyclerViewVehicles: RecyclerView
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var btnAddVehicle: Button

    private val editProfileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadUserProfile()
                loadUserVehicles()
                Toast.makeText(context, "Profile Data Refreshed.", Toast.LENGTH_SHORT).show()
            }
        }

    private val addVehicleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadUserVehicles()
                Toast.makeText(context, "New Vehicle Added.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        tvName = view.findViewById(R.id.textViewProfileName)
        tvEmail = view.findViewById(R.id.textViewProfileEmail)
        tvPhone = view.findViewById(R.id.textViewProfilePhone)
        tvRole = view.findViewById(R.id.textViewProfileRole)
        tvUserId = view.findViewById(R.id.textViewProfileUserId)
        tvDateRegistered = view.findViewById(R.id.textViewDateRegistered)
        btnEditProfile = view.findViewById(R.id.buttonEditProfile)
        btnLogout = view.findViewById(R.id.buttonLogout)

        recyclerViewVehicles = view.findViewById(R.id.recyclerViewVehicles)
        btnAddVehicle = view.findViewById(R.id.buttonAddVehicle)

        recyclerViewVehicles.layoutManager = LinearLayoutManager(requireContext())

        vehicleAdapter = VehicleAdapter(emptyList()) { vehicleToDelete ->
            deleteVehicle(vehicleToDelete)
        }
        recyclerViewVehicles.adapter = vehicleAdapter

        loadUserProfile()
        loadUserVehicles()
        setupListeners()
    }

    private fun loadUserProfile() {
        val sharedPrefs = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString(LoginActivity.PREF_USER_ID_KEY, null)
        currentUserId = userId

        if (userId != null) {
            val user = dbHelper.readUser(userId)

            if (user != null) {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val registeredDate = dateFormat.format(Date(user.dateRegistered))

                tvName.text = "Name/Username: ${user.username}"
                tvEmail.text = "Email: ${user.email}"
                tvPhone.text = "Phone: ${user.phoneNumber}"
                tvRole.text = "Role: ${user.role.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }}"
                tvUserId.text = "User ID: ${user.userId}"
                tvDateRegistered.text = "Registered: $registeredDate"

                btnEditProfile.isEnabled = true
            } else {
                Toast.makeText(context, "Error: User data not found in database.", Toast.LENGTH_LONG).show()
                btnEditProfile.isEnabled = false
            }
        } else {
            Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show()
            tvName.text = "Please log in."
            btnEditProfile.isEnabled = false
        }
    }

    private fun loadUserVehicles() {
        val userId = currentUserId
        if (userId != null) {
            val vehicles = dbHelper.readVehiclesByUserId(userId)
            vehicleAdapter.updateVehicleList(vehicles)
        } else {
            vehicleAdapter.updateVehicleList(emptyList())
        }
    }

    private fun deleteVehicle(vehicle: VehicleDataClass) {
        val rowsDeleted = dbHelper.deleteVehicle(vehicle.vehicleId)

        if (rowsDeleted > 0) {
            Toast.makeText(context, "${vehicle.make} ${vehicle.model} removed.", Toast.LENGTH_SHORT).show()
            loadUserVehicles()
        } else {
            Toast.makeText(context, "Failed to remove vehicle.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        btnEditProfile.setOnClickListener {
            if (currentUserId != null) {
                val intent = Intent(requireContext(), EditProfileActivity::class.java).apply {
                    putExtra(EXTRA_USER_ID, currentUserId)
                }
                editProfileLauncher.launch(intent)
            } else {
                Toast.makeText(context, "Error: Cannot edit profile without a User ID.", Toast.LENGTH_SHORT).show()
            }
        }

        btnAddVehicle.setOnClickListener {
            if (currentUserId != null) {
                val intent = Intent(requireContext(), AddVehicleActivity::class.java).apply {
                    putExtra(EXTRA_USER_ID, currentUserId)
                }
                addVehicleLauncher.launch(intent)
            } else {
                Toast.makeText(context, "Please log in to add vehicles.", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        val sharedPrefs = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().remove(LoginActivity.PREF_USER_ID_KEY).apply()

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Toast.makeText(context, "Logged out successfully.", Toast.LENGTH_SHORT).show()
    }
}