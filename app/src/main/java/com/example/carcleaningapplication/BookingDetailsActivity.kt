package com.example.carcleaningapplication

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.carcleaningapplication.databinding.ActivityBookingDetailsBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingDetailsBinding
    private lateinit var dbHelper: DatabaseHelper
    private var serviceId: String = ""
    private var vendorName: String = ""
    private var packageType: String = ""
    private var baseFare: Double = 0.0
    private var availableDays: String = ""
    private var currentUserId: String? = null
    private lateinit var vehicleDropdown: AutoCompleteTextView
    private lateinit var timeSpinner: Spinner
    private lateinit var editDateSelection: EditText
    private lateinit var userVehicles: List<VehicleDataClass>
    private var selectedVehicleId: String? = null
    private var selectedTimestamp: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Finalize Booking"

        vehicleDropdown = binding.autoCompleteVehicle
        timeSpinner = binding.spinnerTimeSelection
        editDateSelection = binding.editTextDateSelection

        val sharedPrefs = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        currentUserId = sharedPrefs.getString(LoginActivity.PREF_USER_ID_KEY, null)

        if (currentUserId == null) {
            Toast.makeText(this, "Session expired. Please log in.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        fetchServiceDetails(intent)

        displayServiceDetails()

        loadRequiredData()

        editDateSelection.setOnClickListener { showDatePicker() }
        vehicleDropdown.setOnItemClickListener { parent, view, position, id ->
            selectedVehicleId = userVehicles[position].vehicleId
        }

        binding.btnShareDetails.setOnClickListener {
            shareDetails()
        }

        binding.btnBookService.setOnClickListener {
            finalizeBooking()
        }
    }

    private fun fetchServiceDetails(intent: Intent) {
        serviceId = intent.getStringExtra(ServiceAdapter.EXTRA_SERVICE_ID) ?: ""
        vendorName = intent.getStringExtra(ServiceAdapter.EXTRA_VENDOR_NAME) ?: "N/A"
        packageType = intent.getStringExtra(ServiceAdapter.EXTRA_PACKAGE_TYPE) ?: "N/A"
        baseFare = intent.getDoubleExtra(ServiceAdapter.EXTRA_BASE_FARE, 0.0) // Fetch as Double
        availableDays = intent.getStringExtra(ServiceAdapter.EXTRA_AVAILABLE_DAYS) ?: "N/A"

        if (serviceId.isEmpty()) {
            Toast.makeText(this, "Error: Service ID missing. Cannot proceed.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun displayServiceDetails() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MY")).apply {
            maximumFractionDigits = 2
        }
        binding.tvVendorName.text = "Vendor: $vendorName"
        binding.tvPackageType.text = "Package Type: $packageType"
        binding.tvBaseFare.text = "Base Fare: ${currencyFormat.format(baseFare)}"
        binding.tvAvailableDays.text = "Availability: $availableDays"
    }

    private fun loadRequiredData() {

        userVehicles = dbHelper.readVehiclesByUserId(currentUserId!!)

        if (userVehicles.isEmpty()) {
            Toast.makeText(this, "Please add a vehicle to your profile before booking!", Toast.LENGTH_LONG).show()
            binding.btnBookService.isEnabled = false
        }

        val vehicleOptions = userVehicles.map { "${it.make} ${it.model} (${it.year})" }
        val vehicleAdapter = ArrayAdapter(this, R.layout.list_item_dropdown, vehicleOptions)
        vehicleDropdown.setAdapter(vehicleAdapter)

        val timeSlots = listOf("Select Time Slot", "10:00 AM", "12:00 PM", "2:00 PM", "4:00 PM")
        val timeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlots)
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner.adapter = timeAdapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { view, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
                }

                selectedTimestamp = selectedDate.timeInMillis

                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                editDateSelection.setText(dateFormat.format(Date(selectedTimestamp)))

            }, year, month, day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }


    private fun shareDetails() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MY")).apply {
            maximumFractionDigits = 2
        }
        val formattedFare = currencyFormat.format(baseFare)

        val serviceDetailsText = """
            Car Cleaning Service Detail:
            --------------------------------
            Vendor: $vendorName
            Package: $packageType
            Base Fare: $formattedFare
            Available: $availableDays
            
            Book now via the app!
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this Car Cleaning Service!")
            putExtra(Intent.EXTRA_TEXT, serviceDetailsText)
        }

        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(shareIntent, "Share Service Details via..."))
        } else {
            Toast.makeText(this, "No sharing app installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun finalizeBooking() {
        val selectedTimeSlot = timeSpinner.selectedItem.toString()

        if (selectedVehicleId == null || selectedVehicleId!!.isEmpty()) {
            Toast.makeText(this, "Please select a vehicle from the drop-down list.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedTimestamp == 0L) {
            Toast.makeText(this, "Please select a booking date.", Toast.LENGTH_SHORT).show()
            return
        }
        if (timeSpinner.selectedItemPosition <= 0) {
            Toast.makeText(this, "Please select a time slot.", Toast.LENGTH_SHORT).show()
            return
        }

        val timeParts = selectedTimeSlot.split(":", " ") // Example: ["10", "00", "AM"]

        var hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        val ampm = timeParts[2]

        if (ampm.equals("PM", true) && hour != 12) {
            hour += 12
        } else if (ampm.equals("AM", true) && hour == 12) {
            hour = 0 // Case for 12:xx AM (midnight)
        }

        val bookingCalendar = Calendar.getInstance().apply {
            timeInMillis = selectedTimestamp // Start with the selected date (midnight)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val finalTimeMillis = bookingCalendar.timeInMillis

        val resultId = dbHelper.insertAppointment(
            userId = currentUserId!!,
            serviceId = serviceId,
            vehicleId = selectedVehicleId!!,
            appointmentTime = finalTimeMillis,
            totalPrice = baseFare
        )

        if (resultId > 0) {
            Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_HISTORY)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()

        } else {
            Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_LONG).show()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}