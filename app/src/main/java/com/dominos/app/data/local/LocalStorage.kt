package com.dominos.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.dominos.app.data.model.SavedCustomer
import com.google.gson.Gson

class LocalStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dominos_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_CUSTOMER = "saved_customer"
        private const val KEY_LOGGED_IN = "is_logged_in"
        private const val KEY_SAVED_ADDRESSES = "saved_addresses"
    }

    fun saveCustomer(customer: SavedCustomer) {
        prefs.edit().putString(KEY_CUSTOMER, gson.toJson(customer)).apply()
        prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
    }

    fun getCustomer(): SavedCustomer {
        val json = prefs.getString(KEY_CUSTOMER, null) ?: return SavedCustomer()
        return try {
            gson.fromJson(json, SavedCustomer::class.java)
        } catch (e: Exception) {
            SavedCustomer()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun getLastZipCode(): String = prefs.getString("last_zip", "") ?: ""
    fun saveLastZipCode(zip: String) = prefs.edit().putString("last_zip", zip).apply()
    fun getLastStreet(): String = prefs.getString("last_street", "") ?: ""
    fun saveLastStreet(street: String) = prefs.edit().putString("last_street", street).apply()
}
