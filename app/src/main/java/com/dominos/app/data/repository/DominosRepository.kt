package com.dominos.app.data.repository

import com.dominos.app.data.api.RetrofitClient
import com.dominos.app.data.api.StoreDetailResponse
import com.dominos.app.data.model.*

class DominosRepository {
    private val api = RetrofitClient.api
    private val trackerApi = RetrofitClient.trackerApi

    suspend fun findStores(street: String, cityOrZip: String): Result<StoreLocatorResponse> {
        return runCatching {
            val response = api.findStores(street, cityOrZip)
            if (response.isSuccessful && response.body() != null) response.body()!!
            else throw Exception("Store lookup failed: ${response.code()} ${response.message()}")
        }
    }

    suspend fun getStoreProfile(storeID: String): Result<StoreDetailResponse> {
        return runCatching {
            val response = api.getStoreProfile(storeID)
            if (response.isSuccessful && response.body() != null) response.body()!!
            else throw Exception("Store profile failed: ${response.code()}")
        }
    }

    suspend fun getMenu(storeID: String): Result<MenuResponse> {
        return runCatching {
            val response = api.getMenu(storeID)
            if (response.isSuccessful && response.body() != null) response.body()!!
            else {
                val errorBody = response.errorBody()?.string() ?: "unknown"
                throw Exception("Menu fetch failed: ${response.code()} $errorBody")
            }
        }
    }

    suspend fun validateOrder(order: OrderPayload): Result<OrderResponse> {
        return runCatching {
            val response = api.validateOrder(order)
            if (response.isSuccessful && response.body() != null) response.body()!!
            else {
                val body = response.errorBody()?.string() ?: ""
                throw Exception("Validate failed: ${response.code()} $body")
            }
        }
    }

    suspend fun priceOrder(order: OrderPayload): Result<OrderResponse> {
        return runCatching {
            val response = api.priceOrder(order)
            if (response.isSuccessful && response.body() != null) response.body()!!
            else {
                val body = response.errorBody()?.string() ?: ""
                throw Exception("Price failed: ${response.code()} $body")
            }
        }
    }

    suspend fun placeOrder(order: OrderPayload): Result<OrderResponse> {
        return runCatching {
            val response = api.placeOrder(order)
            if (response.isSuccessful && response.body() != null) response.body()!!
            else {
                val body = response.errorBody()?.string() ?: ""
                throw Exception("Place order failed: ${response.code()} $body")
            }
        }
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return runCatching {
            val response = api.login(mapOf("email" to email, "password" to password))
            if (response.isSuccessful) true
            else throw Exception("Login failed: ${response.code()}")
        }
    }

    suspend fun getBraintreeToken(): Result<String> {
        return runCatching {
            val response = api.getBraintreeToken(mapOf())
            if (response.isSuccessful) response.body()?.string() ?: ""
            else throw Exception("Braintree token failed: ${response.code()}")
        }
    }

    suspend fun getTrackerData(orderId: String, phone: String = ""): Result<TrackingResponse> {
        return runCatching {
            trackerApi.getTrackerData(orderId, phone)
        }
    }
}
