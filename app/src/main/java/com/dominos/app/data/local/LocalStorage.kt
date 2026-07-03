package com.dominos.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.dominos.app.data.model.CartItem
import com.dominos.app.data.model.SavedCustomer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dominos_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_CUSTOMER = "saved_customer"
        private const val KEY_LOGGED_IN = "is_logged_in"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_ORDER_HISTORY = "order_history"
        private const val KEY_CART_ITEMS = "cart_items"
        private const val KEY_CART_STORE_ID = "cart_store_id"
        private const val KEY_FAVORITES = "favorite_products"
    }

    fun saveCustomer(customer: SavedCustomer) {
        prefs.edit().putString(KEY_CUSTOMER, gson.toJson(customer)).apply()
        prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
    }

    fun getCustomer(): SavedCustomer {
        val json = prefs.getString(KEY_CUSTOMER, null) ?: return SavedCustomer()
        return try { gson.fromJson(json, SavedCustomer::class.java) } catch (e: Exception) { SavedCustomer() }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun logout() {
        prefs.edit().clear().apply()
    }

    private val KEY_SAVED_ADDRESSES = "saved_addresses"

    fun getLastZipCode(): String = prefs.getString("last_zip", "") ?: ""
    fun saveLastZipCode(zip: String) = prefs.edit().putString("last_zip", zip).apply()
    fun getLastStreet(): String = prefs.getString("last_street", "") ?: ""
    fun saveLastStreet(street: String) = prefs.edit().putString("last_street", street).apply()

    fun getSavedAddresses(): List<String> {
        val json = prefs.getString(KEY_SAVED_ADDRESSES, null) ?: return emptyList()
        return try { gson.fromJson(json, object : TypeToken<List<String>>() {}.type) } catch (e: Exception) { emptyList() }
    }
    fun saveAddress(address: String) {
        val current = getSavedAddresses().toMutableList()
        if (address !in current) current.add(0, address)
        if (current.size > 5) current.removeAt(current.lastIndex)
        prefs.edit().putString(KEY_SAVED_ADDRESSES, gson.toJson(current)).apply()
    }

    fun isDarkMode(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)
    fun setDarkMode(enabled: Boolean) = prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()

    fun getOrderHistory(): List<String> = prefs.getStringSet(KEY_ORDER_HISTORY, emptySet())?.toList() ?: emptyList()
    fun addOrderHistoryEntry(entry: String) {
        val entries = getOrderHistory().toMutableSet()
        entries.add(entry)
        if (entries.size > 50) entries.remove(entries.first())
        prefs.edit().putStringSet(KEY_ORDER_HISTORY, entries).apply()
    }

    fun saveCart(items: List<CartItem>, storeId: String?) {
        prefs.edit().putString(KEY_CART_ITEMS, gson.toJson(items)).apply()
        storeId?.let { prefs.edit().putString(KEY_CART_STORE_ID, it).apply() }
    }

    fun getCartItems(): List<CartItem> {
        val json = prefs.getString(KEY_CART_ITEMS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) { emptyList() }
    }

    fun getCartStoreId(): String? = prefs.getString(KEY_CART_STORE_ID, null)

    fun getFavorites(): Set<String> = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    fun toggleFavorite(code: String, name: String = "") {
        val current = getFavorites().toMutableSet()
        val entry = "$code|$name"
        val existing = current.find { it.startsWith("$code|") }
        if (existing != null) current.remove(existing) else current.add(entry)
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
    }
    fun isFavorite(code: String): Boolean = getFavorites().any { it.startsWith("$code|") }
    fun getFavoriteName(code: String): String = getFavorites().find { it.startsWith("$code|") }?.substringAfter("|") ?: code
}
