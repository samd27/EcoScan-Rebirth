package com.teamecoscan.ecoscanrebirth.data

import android.content.Context

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("ecoscan_prefs", Context.MODE_PRIVATE)

    fun shouldShowWelcome(): Boolean {
        return sharedPreferences.getBoolean("show_welcome", true)
    }

    fun setWelcomeShown() {
        sharedPreferences.edit().putBoolean("show_welcome", false).apply()
    }

    fun resetWelcome() {
        sharedPreferences.edit().putBoolean("show_welcome", true).apply()
    }
}

