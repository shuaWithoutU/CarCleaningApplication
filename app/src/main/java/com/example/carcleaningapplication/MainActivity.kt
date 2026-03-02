package com.example.carcleaningapplication

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.carcleaningapplication.R
import com.example.carcleaningapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val historyFragment = HistoryFragment()
    private val profileFragment = ProfileFragment()

    companion object {
        const val EXTRA_FRAGMENT_TO_SHOW = "FRAGMENT_TO_SHOW"
        const val FRAGMENT_HISTORY = "HISTORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            handleInitialFragment(intent)
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(homeFragment)
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(profileFragment)
                    true
                }
                R.id.nav_history -> {
                    replaceFragment(historyFragment)

                    historyFragment.view?.postDelayed({
                        (historyFragment as? HistoryFragment)?.refreshHistory()
                    }, 50)

                    true
                }
                else -> false
            }
        }
    }

    private fun handleInitialFragment(intent: Intent?) {
        val fragmentToShow = intent?.getStringExtra(EXTRA_FRAGMENT_TO_SHOW)

        if (fragmentToShow == FRAGMENT_HISTORY) {
            binding.bottomNavigationView.selectedItemId = R.id.nav_history
            replaceFragment(historyFragment)

            historyFragment.view?.postDelayed({
                (historyFragment as? HistoryFragment)?.refreshHistory()
                Toast.makeText(this, "Booking Placed Successfully!", Toast.LENGTH_LONG).show()
            }, 50)

        } else {
            replaceFragment(homeFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                Toast.makeText(this, "Search initiated", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_share -> {
                shareApp()
                true
            }
            R.id.action_settings -> {
                binding.bottomNavigationView.selectedItemId = R.id.nav_profile
                true
            }
            R.id.action_logout -> {
                val sharedPrefs = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
                sharedPrefs.edit().remove(LoginActivity.PREF_USER_ID_KEY).apply()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Check out the Car Cleaning App!")
            putExtra(Intent.EXTRA_TEXT, "I'm using this awesome Car Cleaning App for all my bookings. Download it today!")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share app link via..."))
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}