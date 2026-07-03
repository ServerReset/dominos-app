package com.dominos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dominos.app.navigation.Screen
import com.dominos.app.ui.screens.*
import com.dominos.app.ui.theme.DominosTheme
import com.dominos.app.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DominosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DominosApp()
                }
            }
        }
    }
}

@Composable
fun DominosApp() {
    val navController = rememberNavController()
    val accountViewModel: AccountViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val storeViewModel: StoreViewModel = viewModel()
    val menuViewModel: MenuViewModel = viewModel()
    val checkoutViewModel: CheckoutViewModel = viewModel()

    val accountState by accountViewModel.uiState.collectAsState()
    val cartState by cartViewModel.uiState.collectAsState()
    val storeState by storeViewModel.uiState.collectAsState()
    val menuState by menuViewModel.uiState.collectAsState()
    val checkoutState by checkoutViewModel.uiState.collectAsState()

    val cartItemCount = cartViewModel.getItemCount()

    NavHost(
        navController = navController,
        startDestination = if (accountState.isLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { email, password ->
                    accountViewModel.login(email, password)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Account.route) {
            AccountScreen(
                state = accountState,
                onFieldUpdate = { field, value -> accountViewModel.updateField(field, value) },
                onSave = {
                    accountViewModel.saveProfile()
                    navController.popBackStack()
                },
                onLogout = {
                    accountViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                state = storeState,
                onSearchQueryChange = { storeViewModel.updateSearchQuery(it) },
                onZipChange = { storeViewModel.updateZipQuery(it) },
                onSearch = {
                    storeViewModel.searchStores(
                        storeState.searchQuery.ifBlank { "1 Main St" },
                        storeState.zipQuery
                    )
                },
                onCartClick = { navController.navigate(Screen.Cart.route) },
                onAccountClick = { navController.navigate(Screen.Account.route) },
                onViewStores = {
                    navController.navigate(Screen.Stores.route)
                },
                cartItemCount = cartItemCount
            )
        }

        composable(Screen.Stores.route) {
            StoresScreen(
                stores = storeState.stores,
                isLoading = storeState.isLoading,
                onStoreSelected = { storeId ->
                    navController.navigate(Screen.Menu.createRoute(storeId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Menu.route) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
            LaunchedEffect(storeId) {
                if (menuState.menuResponse == null || menuState.storeId != storeId) {
                    menuViewModel.loadMenu(storeId)
                }
            }
            MenuScreen(
                state = menuState,
                onCategorySelected = { index -> menuViewModel.selectCategory(index) },
                onProductClick = { productCode ->
                    navController.navigate("customize/$storeId/$productCode")
                },
                onCartClick = { navController.navigate(Screen.Cart.route) },
                onBack = { navController.popBackStack() },
                cartItemCount = cartItemCount
            )
        }

        composable(
            route = "customize/{storeId}/{productCode}",
            arguments = listOf(
                navArgument("storeId") { type = NavType.StringType },
                navArgument("productCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
            val productCode = backStackEntry.arguments?.getString("productCode") ?: ""
            val item = menuState.selectedCategoryProducts.find { it.productCode == productCode }
            val categoryCode = menuState.flatCategories.getOrNull(menuState.selectedCategoryIndex)?.code
            val fullItem = item?.copy(
                flavors = menuViewModel.getProductFlavors(productCode, categoryCode),
                sizes = menuViewModel.getProductSizes(productCode)
            )
            CustomizeProductScreen(
                storeId = storeId,
                item = fullItem,
                onAddToCart = { cartItem, sId ->
                    cartViewModel.addItem(cartItem, sId)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                items = cartState.items,
                onUpdateQuantity = { id, delta -> cartViewModel.updateQuantity(id, delta) },
                onRemoveItem = { id -> cartViewModel.removeItem(id) },
                onCheckout = {
                    val sid = cartState.storeId ?: return@CartScreen
                    navController.navigate(Screen.Checkout.createRoute(sid))
                },
                onContinueShopping = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "checkout/{storeId}",
            arguments = listOf(navArgument("storeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
            CheckoutScreen(
                storeId = storeId,
                accountState = accountState,
                orderState = checkoutState,
                cartItems = cartState.items,
                onUpdateField = { field, value -> accountViewModel.updateField(field, value) },
                onSaveProfile = { accountViewModel.saveProfile() },
                onPlaceOrder = {
                    checkoutViewModel.placeOrder(
                        storeId = storeId,
                        customer = accountViewModel.getCustomerForOrder(),
                        cartItems = cartState.items,
                        serviceMethod = checkoutState.serviceMethod
                    )
                },
                onServiceMethodChange = { checkoutViewModel.setServiceMethod(it) },
                onViewTracking = { orderId ->
                    if (orderId.isNotBlank()) {
                        navController.navigate(Screen.Tracking.createRoute(orderId))
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "tracking/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            TrackingScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
