package com.example.notifications

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.notifications.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup your home activity here
    }

    fun onNotificationBellClicked(view: View) {
        startActivity(Intent(this, NotificationsActivity::class.java))
        overridePendingTransition(0, 0)
    }
}