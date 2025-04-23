package com.example.myspence

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_EMAIL = "email"
        private const val IS_LOGGED_IN = "is_logged_in"
    }

    fun saveUser(email: String) {
        prefs.edit().apply {
            putString(KEY_EMAIL, email)
            putBoolean(IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUserEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun isLoggedIn(): Boolean = prefs.getBoolean(IS_LOGGED_IN, false)

    fun logout() {
        prefs.edit().clear().apply()
    }
}
