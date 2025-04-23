package com.example.myspence

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Optional: add a splash screen layout
        setContentView(R.layout.activity_splash)

        val sessionManager = SessionManager(this)
        val sharedPref = getSharedPreferences("MySpencePrefs", MODE_PRIVATE)

        lifecycleScope.launch {
            delay(2000) // 2 seconds splash duration
            if (sharedPref.getBoolean("isLoggedIn", false)) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@launch
            } else {
                startActivity(Intent(this@SplashActivity, Login::class.java))
            }
            finish()
        }
    }
}
