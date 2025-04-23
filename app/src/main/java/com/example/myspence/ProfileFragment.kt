package com.example.myspence

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPreferences = requireContext().getSharedPreferences("MySpencePrefs", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("email", "User")
        view.findViewById<TextView>(R.id.textUsername).text = "Welcome!"


        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // Clear shared preferences
            sharedPreferences.edit().clear().apply()

            // Navigate to login and clear back stack
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        val editTextFeedback = view.findViewById<EditText>(R.id.editTextFeedback)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val submitFeedback = view.findViewById<Button>(R.id.btnSubmitFeedback)

        submitFeedback.setOnClickListener {
            val feedbackText = editTextFeedback.text.toString().trim()
            val rating = ratingBar.rating.toInt()

            if (feedbackText.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter feedback", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (rating == 0) {
                Toast.makeText(requireContext(), "Please provide a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FeedbackHelper.saveFeedback(requireContext(), username ?: "Anonymous", "$feedbackText\nRating: $rating/5")
            Toast.makeText(requireContext(), "Feedback submitted! Thank you.", Toast.LENGTH_SHORT).show()
            editTextFeedback.text.clear()
            ratingBar.rating = 0f
        }

    }
}
