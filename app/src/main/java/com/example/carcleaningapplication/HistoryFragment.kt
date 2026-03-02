package com.example.carcleaningapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carcleaningapplication.databinding.FragmentHistoryBinding
import java.util.Locale

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: String? = null

    private var appointmentList: List<AppointmentDataClass> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        val sharedPrefs = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        currentUserId = sharedPrefs.getString(LoginActivity.PREF_USER_ID_KEY, null)

        if (currentUserId != null) {
            loadAppointments()
        }

        val recyclerView = binding.recyclerViewHistory
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (appointmentList.isNotEmpty()) {
            recyclerView.adapter = HistoryAdapter(appointmentList) { appointment ->
                cancelAppointment(appointment)
            }
            binding.tvHistoryHeader.text = "Your Order History"
        } else {
            recyclerView.adapter = HistoryAdapter(emptyList()) { }
            binding.tvHistoryHeader.text = "No Ongoing Orders Yet! Book your first service."
        }
    }

    private fun loadAppointments() {
        if (currentUserId != null) {
            appointmentList = dbHelper.readAppointmentsByUserId(currentUserId!!)
        } else {
            appointmentList = emptyList()
        }
    }

    private fun cancelAppointment(appointment: AppointmentDataClass) {
        if (appointment.status.toUpperCase(Locale.ROOT) != "CONFIRMED") {
            Toast.makeText(requireContext(), "Only confirmed bookings can be cancelled.", Toast.LENGTH_SHORT).show()
            return
        }

        val rowsAffected = dbHelper.deleteAppointment(appointment.appointmentId)

        if (rowsAffected > 0) {
            Toast.makeText(
                requireContext(),
                "Booking DELETED.",
                Toast.LENGTH_LONG
            ).show()
            refreshHistory()
        } else {
            Toast.makeText(requireContext(), "Deletion failed. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }


    fun refreshHistory() {
        if (_binding == null) {
            return
        }

        loadAppointments()

        val recyclerView = binding.recyclerViewHistory

        if (appointmentList.isNotEmpty()) {
            recyclerView.adapter = HistoryAdapter(appointmentList) { appointment ->
                cancelAppointment(appointment)
            }
            binding.tvHistoryHeader.text = "Your Order History"
        } else {
            recyclerView.adapter = HistoryAdapter(emptyList()) { }
            binding.tvHistoryHeader.text = "No Ongoing Orders Yet! Book your first service."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}