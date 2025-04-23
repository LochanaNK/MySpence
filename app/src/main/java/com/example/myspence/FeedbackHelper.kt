package com.example.myspence

import android.content.Context

object FeedbackHelper {
    private const val PREF_NAME = "feedback_prefs"
    private const val FEEDBACK_KEY = "feedback_list"

    fun saveFeedback(context: Context, username: String, feedback: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentFeedback = prefs.getStringSet(FEEDBACK_KEY, mutableSetOf()) ?: mutableSetOf()
        currentFeedback.add("$username: $feedback")
        prefs.edit().putStringSet(FEEDBACK_KEY, currentFeedback).apply()
    }

    fun getAllFeedback(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(FEEDBACK_KEY, mutableSetOf()) ?: emptySet()
    }

    fun saveAllFeedback(context: Context, feedbackSet: Set<String>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(FEEDBACK_KEY, feedbackSet).apply()
    }
}
