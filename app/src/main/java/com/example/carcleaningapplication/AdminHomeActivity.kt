package com.example.carcleaningapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carcleaningapplication.databinding.ActivityAdminHomeBinding
import java.util.Locale

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: AdminUserAdapter
    private var userList: List<UserDataClass> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setSupportActionBar(binding.toolbarAdminHome)
        supportActionBar?.title = "Admin Dashboard"

        setupRecyclerView()
        loadUsers()
        setupBottomNavigation()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu defined in admin_menu_toolbar.xml
        menuInflater.inflate(R.menu.admin_menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_admin_logout -> {
                performAdminLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performAdminLogout() {
        val sharedPrefs = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().remove(LoginActivity.PREF_USER_ID_KEY).apply()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        Toast.makeText(this, "Admin logged out.", Toast.LENGTH_SHORT).show()
        finish()
    }


    private fun setupRecyclerView() {
        // Initialize adapter with empty list and the delete callback
        adapter = AdminUserAdapter(userList) { userIdToDelete ->
            deleteUser(userIdToDelete)
        }

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUsers.adapter = adapter
    }

    private fun loadUsers() {
        userList = dbHelper.readAllUsers()

        val currentUserId = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE).getString(LoginActivity.PREF_USER_ID_KEY, null)
        val filteredList = userList.filter { it.userId != currentUserId }

        adapter.updateData(filteredList)

        binding.tvAdminHeader.text = "Registered Users (${filteredList.size})"
    }

    private fun deleteUser(userId: String) {
        val rowsAffected = dbHelper.deleteUser(userId)

        if (rowsAffected > 0) {
            Toast.makeText(this, "User deleted successfully (ID: ${userId.take(4)}...).", Toast.LENGTH_SHORT).show()
            loadUsers() // Reload and refresh the list
        } else {
            Toast.makeText(this, "Failed to delete user.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomAdminNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_users -> {
                    loadUsers()
                    true
                }
                R.id.nav_admin_vendors -> {
                    startActivity(Intent(this, AddVendorActivity::class.java))
                    true
                }
                R.id.nav_admin_services -> {
                    startActivity(Intent(this, AddServiceActivity::class.java))
                    true
                }
                else -> false
            }
        }
        binding.bottomAdminNavigation.selectedItemId = R.id.nav_admin_users
    }
}