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
            else throw Exception("Store lookup failed: ${response.code()}")
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
            else throw Exception("Menu fetch failed: ${response.code()}")
        }
    }

    suspend fun getCoupon(storeID: String, couponID: String): Result<String> {
        return runCatching {
            val response = api.getCoupon(storeID, couponID)
            if (response.isSuccessful) response.body()?.string() ?: ""
            else throw Exception("Coupon not found: ${response.code()}")
        }
    }

    suspend fun validateOrder(order: OrderPayload): Result<OrderResponse> {
        return runCatching {
            val response = api.validateOrder(mapOf("Order" to order))
            if (response.isSuccessful && response.body() != null) response.body()!!
            else throw Exception("Validate failed: ${response.code()} ${response.errorBody()?.string()}")
        }
    }

    suspend fun priceOrder(order: OrderPayload): Result<OrderResponse> {
        return runCatching {
            val response = api.priceOrder(mapOf("Order" to order))
            if (response.isSuccessful && response.body() != null) response.body()!!
            else throw Exception("Price failed: ${response.code()} ${response.errorBody()?.string()}")
        }
    }

    suspend fun placeOrder(order: OrderPayload): Result<OrderResponse> {
        return runCatching {
            val response = api.placeOrder(mapOf("Order" to order))
            if (response.isSuccessful && response.body() != null) response.body()!!
            else throw Exception("Place failed: ${response.code()} ${response.errorBody()?.string()}")
        }
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return runCatching {
            throw Exception("Domino's API uses guest ordering only. No login required. Sign in to see local history.")
        }
    }

    suspend fun getBraintreeToken(): Result<String> {
        return runCatching {
            val response = api.getBraintreeToken(mapOf())
            if (response.isSuccessful) response.body()?.string() ?: ""
            else throw Exception("Braintree token failed: ${response.code()}")
        }
    }

    suspend fun getTrackerData(storeId: String, orderKey: String): Result<TrackingResponse> {
        return runCatching { trackerApi.getTrackerData(storeId, orderKey) }
    }
}
