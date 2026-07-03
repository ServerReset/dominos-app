package com.dominos.app.data.repository

import com.dominos.app.data.api.RetrofitClient
import com.dominos.app.data.api.StoreDetailResponse
import com.dominos.app.data.model.*

class DominosRepository {
    private val api = RetrofitClient.api

    suspend fun findStores(street: String, cityOrZip: String): Result<StoreLocatorResponse> {
        return try {
            val response = api.findStores(street, cityOrZip)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Store lookup failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStoreProfile(storeID: String): Result<StoreDetailResponse> {
        return try {
            val response = api.getStoreProfile(storeID)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Store profile failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMenu(storeID: String): Result<MenuResponse> {
        return try {
            val response = api.getMenu(storeID)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "unknown"
                Result.failure(Exception("Menu fetch failed: ${response.code()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun validateOrder(order: OrderPayload): Result<OrderResponse> {
        return try {
            val response = api.validateOrder(order)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val body = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Validate failed: ${response.code()} $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun priceOrder(order: OrderPayload): Result<OrderResponse> {
        return try {
            val response = api.priceOrder(order)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val body = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Price failed: ${response.code()} $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun placeOrder(order: OrderPayload): Result<OrderResponse> {
        return try {
            val response = api.placeOrder(order)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val body = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Place order failed: ${response.code()} $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            val response = api.login(mapOf("email" to email, "password" to password))
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
