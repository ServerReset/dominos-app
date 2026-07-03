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

    @GET("store/{storeID}/coupons")
    suspend fun getCoupons(
        @Path("storeID") storeID: String
    ): Response<ResponseBody>

    @POST("validate-order")
    suspend fun validateOrder(@Body body: Map<String, OrderPayload>): Response<OrderResponse>

    @POST("price-order")
    suspend fun priceOrder(@Body body: Map<String, OrderPayload>): Response<OrderResponse>

    @POST("place-order")
    suspend fun placeOrder(@Body body: Map<String, OrderPayload>): Response<OrderResponse>

    @POST("paymentGatewayService/braintree/token")
    suspend fun getBraintreeToken(@Body body: Map<String, String>): Response<ResponseBody>

    @POST("customer/login")
    suspend fun login1(@Body credentials: Map<String, String>): Response<ResponseBody>

    @POST("login")
    suspend fun login2(@Body credentials: Map<String, String>): Response<ResponseBody>

    @POST("authenticate")
    suspend fun login3(@Body credentials: Map<String, String>): Response<ResponseBody>

    @POST("customer/session")
    suspend fun login4(@Body credentials: Map<String, String>): Response<ResponseBody>

    @GET("customer/profile")
    suspend fun getProfile(@Header("Cookie") cookie: String = ""): Response<ResponseBody>
}
