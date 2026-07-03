package com.dominos.app.data.api

import com.dominos.app.data.model.TrackingResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DominosTrackerApi {
    @GET("tracker?{query}")
    suspend fun getTrackerData(
        @retrofit2.http.Query("orderID") orderID: String,
        @retrofit2.http.Query("phone") phone: String = ""
    ): TrackingResponse
}
