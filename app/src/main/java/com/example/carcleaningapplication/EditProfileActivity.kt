package com.example.carcleaningapplication

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        dbHelper = DatabaseHelper(this)
        editName = findViewById(R.id.editTextEditName)
        editEmail = findViewById(R.id.editTextEditEmail)
        editPhone = findViewById(R.id.editTextEditPhone)
        btnSave = findViewById(R.id.buttonSaveProfile)
        btnCancel = findViewById(R.id.buttonCancelEdit)

        currentUserId = intent.getStringExtra(ProfileFragment.EXTRA_USER_ID)

        if (currentUserId == null) {
            Toast.makeText(this, "Error: User ID not provided.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadCurrentProfileData(currentUserId!!)

        btnSave.setOnClickListener { saveChanges() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun loadCurrentProfileData(userId: String) {
        val user = dbHelper.readUser(userId)
        if (user != null) {
            // Pre-fill EditText fields with current data
            editName.setText(user.username)
            editEmail.setText(user.email)
            editPhone.setText(user.phoneNumber)
        } else {
            Toast.makeText(this, "Failed to load current profile data.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveChanges() {
        val newName = editName.text.toString().trim()
        val newEmail = editEmail.text.toString().trim()
        val newPhone = editPhone.text.toString().trim()

        if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val rowsAffected = dbHelper.updateUser(
            userId = currentUserId!!,
            username = newName,
            email = newEmail,
            phoneNumber = newPhone
        )

        if (rowsAffected > 0) {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
           setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Update failed. No changes or error occurred.", Toast.LENGTH_LONG).show()
        }
    }
}