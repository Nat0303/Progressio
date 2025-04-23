package com.example.notifications

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notifications.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationBadge: BadgeDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupFloatingActionButton()
        updateNotificationBadge(3) // Initialize with sample data
    }

    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = binding.navView

        // Badge for notifications tab
        notificationBadge = navView.getOrCreateBadge(R.id.navigation_notifications)
        notificationBadge.backgroundColor = resources.getColor(R.color.colorPrimary)
        notificationBadge.badgeTextColor = resources.getColor(android.R.color.white)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on home - do nothing
                    true
                }
                R.id.navigation_tasks -> {
                    startActivity(Intent(this, TasksActivity::class.java))
                    overridePendingTransition(0, 0)
                    false
                }
                R.id.navigation_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    overridePendingTransition(0, 0)
                    false
                }
                R.id.navigation_history, R.id.navigation_profile -> {
                    // Do nothing - other team will handle these
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFloatingActionButton() {
        binding.fab.setOnClickListener {
            // Handle FAB click if needed
        }
    }

    fun updateNotificationBadge(count: Int) {
        notificationBadge.isVisible = count > 0
        notificationBadge.number = count
    }
}