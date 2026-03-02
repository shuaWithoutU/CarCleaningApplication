package com.example.carcleaningapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.carcleaningapplication.R // Assuming R is available from the same package


class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

       Handler(Looper.getMainLooper()).postDelayed({

           val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)

            finish()

        }, SPLASH_DELAY)
    }
}