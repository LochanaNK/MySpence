package com.example.myspence

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.FileReader


class SettingsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val themeSwitch = view.findViewById<Switch>(R.id.switchTheme)
        val btnViewFeedback = view.findViewById<Button>(R.id.btnViewFeedback)
        val btnClearData = view.findViewById<Button>(R.id.btnClearData)
        val textAppVersion = view.findViewById<TextView>(R.id.textAppVersion)
        val btnBackup = view.findViewById<Button>(R.id.btnBackup)
        val btnRestore = view.findViewById<Button>(R.id.btnRestore)

        // Load and set app version
        val version = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName
        textAppVersion.text = "App Version: $version"

        // Dark mode switch
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        themeSwitch.isChecked = isDarkMode
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
        }

        btnViewFeedback.setOnClickListener {
            val feedbackList = FeedbackHelper.getAllFeedback(requireContext())
            if (feedbackList.isEmpty()) {
                Toast.makeText(requireContext(), "No feedback found.", Toast.LENGTH_SHORT).show()
            } else {
                val message = feedbackList.joinToString("\n\n")
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        btnClearData.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            Toast.makeText(requireContext(), "All data cleared", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), Login::class.java))
            activity?.finish()
        }

        btnBackup.setOnClickListener {
            backupData()
        }

        btnRestore.setOnClickListener {
            restoreData()
        }
    }

    private fun backupData() {
        val data = JSONObject()

        val email = sharedPreferences.getString("email", "") ?: ""
        val darkMode = sharedPreferences.getBoolean("dark_mode", false)
        val feedbackList = FeedbackHelper.getAllFeedback(requireContext())

        data.put("email", email)
        data.put("dark_mode", darkMode)
        data.put("feedback", feedbackList)

        try {
            // Using app-specific directory for external files
            val backupDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (backupDir != null) {
                if (!backupDir.exists()) {
                    backupDir.mkdirs()  // Create the directory if it doesn't exist
                }

                val file = File(backupDir, "backup_myspence.json")
                val writer = FileWriter(file)
                writer.write(data.toString())
                writer.close()

                Log.d("BackupPath", "Backup saved to: ${file.absolutePath}")
                Toast.makeText(requireContext(), "Backup saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Backup failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun restoreData() {
        try {
            val backupDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(backupDir, "backup_myspence.json")
            if (!file.exists()) {
                Toast.makeText(requireContext(), "Backup file not found!", Toast.LENGTH_SHORT).show()
                return
            }

            val reader = FileReader(file)
            val content = reader.readText()
            reader.close()

            val json = JSONObject(content)
            sharedPreferences.edit().apply {
                putString("email", json.getString("email"))
                putBoolean("dark_mode", json.getBoolean("dark_mode"))
                apply()
            }

            val feedbackArray = json.getJSONArray("feedback")
            val feedbackSet = mutableSetOf<String>()
            for (i in 0 until feedbackArray.length()) {
                feedbackSet.add(feedbackArray.getString(i))
            }
            FeedbackHelper.saveAllFeedback(requireContext(), feedbackSet)

            Toast.makeText(requireContext(), "Data restored successfully!", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Restore failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

