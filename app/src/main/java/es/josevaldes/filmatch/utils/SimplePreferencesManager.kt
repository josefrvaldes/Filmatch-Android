package es.josevaldes.filmatch.utils

import android.content.Context

class SimplePreferencesManager(val context: Context) {


    private fun saveString(key: String, value: String) {
        val sharedPref = context.getSharedPreferences("filmatch", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    private fun getString(key: String): String? {
        val sharedPref = context.getSharedPreferences("filmatch", Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }

    private fun saveInt(key: String, value: Int) {
        val sharedPref = context.getSharedPreferences("filmatch", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    private fun getInt(key: String): Int {
        val sharedPref = context.getSharedPreferences("filmatch", Context.MODE_PRIVATE)
        return sharedPref.getInt(key, 0)
    }

    private fun saveBoolean(key: String, value: Boolean) {
        val sharedPref = context.getSharedPreferences("filmatch", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    private fun getBoolean(key: String): Boolean {
        val sharedPref = context.getSharedPreferences("filmatch", Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, false)
    }

    private fun clear() {
        val sharedPref = context.getSharedPreferences("filmatch", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

    private fun remove(key: String) {
        val sharedPref = context.getSharedPreferences("filmatch", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove(key)
            apply()
        }
    }

    fun setOnboardingFinished() {
        saveBoolean("onboarding_finished", true)
    }

    fun isOnboardingFinished(): Boolean {
        return getBoolean("onboarding_finished")
    }
}