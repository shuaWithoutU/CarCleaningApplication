package com.example.carcleaningapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    companion object {
        const val PREF_USER_ID_KEY = "LOGGED_IN_USER_ID"
        const val PREF_NAME = "AppPrefs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        val emailField = findViewById<EditText>(R.id.editTextEmail)
        val passwordField = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val goToRegister = findViewById<TextView>(R.id.textViewGoToRegister)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = dbHelper.authenticateUser(email, password)

            if (userId != null) {
                val userRole = dbHelper.getUserRole(userId)

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                val sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                sharedPrefs.edit().putString(PREF_USER_ID_KEY, userId).apply()

                val intent: Intent = when (userRole?.toLowerCase(Locale.ROOT)) {
                    "admin" -> {
                        Intent(this, AdminHomeActivity::class.java)
                    }
                    "customer" -> {
                        Intent(this, MainActivity::class.java)
                    }
                    else -> {
                        Toast.makeText(this, "Unknown user role. Contact support.", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }

                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Login failed. Check email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        goToRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}