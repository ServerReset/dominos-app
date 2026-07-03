package com.dominos.app.data.api

import com.dominos.app.data.model.TrackingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DominosTrackerApi {
    @GET("orderstorage/GetTrackerData")
    suspend fun getTrackerData(
        @Query("StoreID") storeID: String,
        @Query("OrderKey") orderKey: String
    ): TrackingResponse
    
    @GET("orderstorage/GetTrackerData")
    suspend fun getTrackerByPhone(
        @Query("Phone") phone: String
    ): TrackingResponse
}
