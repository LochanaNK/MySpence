package com.example.myspence

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import BudgetViewModel
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var budgetViewModel: BudgetViewModel

    private val tabTitles = listOf("Transactions", "Analysis", "Budget")

    private val CHANNEL_ID = "budget_alert_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        budgetViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(BudgetViewModel::class.java)

        createNotificationChannel()

        observeBudget()

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    viewPager.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                    findViewById<View>(R.id.fragmentContainer).visibility = View.GONE
                    supportFragmentManager.beginTransaction().removeFragments().commit()
                    true
                }
                R.id.nav_profile -> {
                    switchToFragment(ProfileFragment())
                    true
                }
                R.id.nav_settings -> {
                    switchToFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeBudget() {
        budgetViewModel.currentBudget.observe(this) { budget ->
            budget?.let {
                budgetViewModel.monthlyExpenses.observe(this) { totalSpent ->
                    val spent = totalSpent ?: 0.0
                    val percent = (spent / budget.amount) * 100

                    when {
                        percent >= 100 -> {
                            Toast.makeText(this, "🚨 Budget exceeded!", Toast.LENGTH_LONG).show()
                            showNotification("🚨 Budget exceeded!","You have exceeded the budget limit.")
                        }
                        percent >= 80 -> {
                            Toast.makeText(this, "⚠️ Almost at your budget limit!", Toast.LENGTH_LONG
                            ).show()
                            showNotification("⚠️ Almost at your budget limit!","You are nearing your budget limit spend carefully!")

                        }
                    }
                }
            }
        }
    }

    private fun showNotification(title: String, content: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_svg)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)  // Notification ID: 1
    }

    private fun createNotificationChannel() {
        // Only create the channel on Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Budget Alert Channel"
            val descriptionText = "Notifications for budget limit alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun switchToFragment(fragment: Fragment) {
        viewPager.visibility = View.GONE
        tabLayout.visibility = View.GONE
        findViewById<View>(R.id.fragmentContainer).visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun androidx.fragment.app.FragmentTransaction.removeFragments(): androidx.fragment.app.FragmentTransaction {
        supportFragmentManager.fragments.forEach {
            remove(it)
        }
        return this
    }
}
