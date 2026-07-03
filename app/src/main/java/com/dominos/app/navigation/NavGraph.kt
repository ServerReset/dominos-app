package com.dominos.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Account : Screen("account")
    object Home : Screen("home")
    object Stores : Screen("stores")
    object StoreDetail : Screen("store_detail/{storeIndex}") {
        fun createRoute(storeIndex: Int) = "store_detail/$storeIndex"
    }
    object Menu : Screen("menu/{storeId}") {
        fun createRoute(storeId: String) = "menu/$storeId"
    }
    object MenuCategory : Screen("menu/{storeId}/{categoryIndex}") {
        fun createRoute(storeId: String, categoryIndex: Int) = "menu/$storeId/$categoryIndex"
    }
    object CustomizeProduct : Screen("customize/{storeId}/{productCode}") {
        fun createRoute(storeId: String, productCode: String) = "customize/$storeId/$productCode"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout/{storeId}") {
        fun createRoute(storeId: String) = "checkout/$storeId"
    }
    object Tracking : Screen("tracking/{storeId}/{orderId}") {
        fun createRoute(storeId: String, orderId: String) = "tracking/$storeId/$orderId"
    }
    object OrderHistory : Screen("order_history")
    object Favorites : Screen("favorites")
}
