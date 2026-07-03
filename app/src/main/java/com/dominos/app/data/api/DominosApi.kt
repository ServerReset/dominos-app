package com.dominos.app.data.api

import com.dominos.app.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface DominosApi {

    @GET("store-locator")
    suspend fun findStores(
        @Query("s") street: String,
        @Query("c") cityOrZip: String,
        @Query("type") type: String = "Delivery"
    ): Response<StoreLocatorResponse>

    @GET("store/{storeID}/profile")
    suspend fun getStoreProfile(
        @Path("storeID") storeID: String
    ): Response<StoreDetailResponse>

    @GET("store/{storeID}/menu?lang=en&structured=true")
    suspend fun getMenu(
        @Path("storeID") storeID: String
    ): Response<MenuResponse>

    @POST("validate-order")
    suspend fun validateOrder(@Body body: Map<String, OrderPayload>): Response<OrderResponse>

    @POST("price-order")
    suspend fun priceOrder(@Body body: Map<String, OrderPayload>): Response<OrderResponse>

    @POST("place-order")
    suspend fun placeOrder(@Body body: Map<String, OrderPayload>): Response<OrderResponse>

    @POST("paymentGatewayService/braintree/token")
    suspend fun getBraintreeToken(@Body body: Map<String, String>): Response<ResponseBody>

    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): Response<ResponseBody>
}
