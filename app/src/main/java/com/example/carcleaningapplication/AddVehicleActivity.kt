package com.example.carcleaningapplication

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.UUID

class AddVehicleActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editMake: EditText
    private lateinit var editModel: EditText
    private lateinit var editYear: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)

        dbHelper = DatabaseHelper(this)
        editMake = findViewById(R.id.editTextVehicleMake)
        editModel = findViewById(R.id.editTextVehicleModel)
        editYear = findViewById(R.id.editTextVehicleYear)
        btnSave = findViewById(R.id.buttonSaveVehicle)
        btnCancel = findViewById(R.id.buttonCancelVehicle)

        currentUserId = intent.getStringExtra(ProfileFragment.EXTRA_USER_ID)

        if (currentUserId == null) {
            Toast.makeText(this, "Error: User ID not found. Cannot add vehicle.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnSave.setOnClickListener { saveVehicle() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun saveVehicle() {
        val make = editMake.text.toString().trim()
        val model = editModel.text.toString().trim()
        val year = editYear.text.toString().trim()

        if (make.isEmpty() || model.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Please fill in all vehicle details.", Toast.LENGTH_SHORT).show()
            return
        }

        val newVehicleId = UUID.randomUUID().toString()

        val newVehicle = VehicleDataClass(
            vehicleId = newVehicleId,
            userId = currentUserId!!,
            make = make,
            model = model,
            year = year
        )

        val success = dbHelper.insertVehicle(newVehicle)

        if (success) {
            Toast.makeText(this, "Vehicle added successfully!", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Failed to add vehicle.", Toast.LENGTH_LONG).show()
        }
    }
}