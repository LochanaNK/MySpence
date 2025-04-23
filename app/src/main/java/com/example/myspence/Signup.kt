package com.example.myspence

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class Signup : AppCompatActivity() {
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val emailInput = findViewById<EditText>(R.id.editTextEmail)
        val passwordInput = findViewById<EditText>(R.id.editTextPassword)
        val confirmInput = findViewById<EditText>(R.id.editTextConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val textLogin = findViewById<TextView>(R.id.textLogin)

        userDao = AppDatabase.getDatabase(this).userDao()

        btnRegister.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val confirm = confirmInput.text.toString()

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    val existingUser = userDao.getUserByEmail(email)
                    if (existingUser != null) {
                        runOnUiThread {
                            Toast.makeText(this@Signup, "User already exists", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        userDao.register(User(email, password))
                        runOnUiThread {
                            Toast.makeText(this@Signup, "Registered successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        }

        textLogin.setOnClickListener {
            finish() // go back to login screen
        }
    }
}
