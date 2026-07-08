package com.dominos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dominos.app.data.model.CartItem
import com.dominos.app.navigation.Screen
import com.dominos.app.ui.theme.SoftDamping
import com.dominos.app.ui.components.BottomNavItem
import com.dominos.app.ui.components.DominosBottomBar
import com.dominos.app.ui.screens.*
import com.dominos.app.ui.theme.DominosTheme
import com.dominos.app.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            val accountViewModel: AccountViewModel = viewModel()
            val accountState by accountViewModel.uiState.collectAsState()
            LaunchedEffect(accountState.isDarkMode) { isDarkMode = accountState.isDarkMode }

            DominosTheme(darkTheme = isDarkMode) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DominosApp(accountViewModel = accountViewModel, isDarkMode = isDarkMode, onDarkModeChange = { isDarkMode = it })
                }
            }
        }
    }
}

@Composable
fun DominosApp(
    accountViewModel: AccountViewModel = viewModel(),
    isDarkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    val storeViewModel: StoreViewModel = viewModel()
    val menuViewModel: MenuViewModel = viewModel()
    val checkoutViewModel: CheckoutViewModel = viewModel()
    val orderHistoryViewModel: OrderHistoryViewModel = viewModel()
    val trackingViewModel: TrackingViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()

    val accountState by accountViewModel.uiState.collectAsState()
    val cartState by cartViewModel.uiState.collectAsState()
    val storeState by storeViewModel.uiState.collectAsState()
    val menuState by menuViewModel.uiState.collectAsState()
    val checkoutState by checkoutViewModel.uiState.collectAsState()
    val orderHistoryState by orderHistoryViewModel.uiState.collectAsState()
    val trackingState by trackingViewModel.uiState.collectAsState()
    val favoritesState by favoritesViewModel.uiState.collectAsState()

    val cartItemCount = cartViewModel.getItemCount()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route, Screen.Account.route, Screen.Cart.route,
        Screen.Stores.route, Screen.OrderHistory.route, Screen.Favorites.route,
        "menu/{storeId}", "store_detail/{storeIndex}"
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                DominosBottomBar(
                    items = listOf(
                        BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
                        BottomNavItem("Cart", Icons.Default.ShoppingCart, Screen.Cart.route, badgeCount = cartItemCount),
                        BottomNavItem("Account", Icons.Default.Person, Screen.Account.route)
                    ),
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) { scaffoldPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(scaffoldPadding),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = spring(dampingRatio = SoftDamping, stiffness = 300f)) + fadeIn(animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = spring(dampingRatio = SoftDamping, stiffness = 300f)) + fadeOut(animationSpec = tween(250)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = spring(dampingRatio = SoftDamping, stiffness = 300f)) + fadeIn(animationSpec = tween(250)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = spring(dampingRatio = SoftDamping, stiffness = 300f)) + fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onSplashFinished = {
                    val dest = if (accountState.isLoggedIn) Screen.Home.route else Screen.Login.route
                    navController.navigate(dest) { popUpTo(Screen.Splash.route) { inclusive = true } }
                })
            }

            composable(Screen.Login.route) {
            LaunchedEffect(accountState.loginSuccess) {
                if (accountState.loginSuccess) {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                }
            }

            LoginScreen(
                onGuestContinue = {
                    accountViewModel.guestContinue()
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onLogin = { email, password -> accountViewModel.login(email, password) },
                isLoading = accountState.isLoading,
                error = accountState.error
            )
            }

            composable(Screen.Account.route) {
            AccountScreen(
                state = accountState, onUpdateField = { field, value -> accountViewModel.updateField(field, value) },
                onSave = { accountViewModel.saveProfile(); navController.popBackStack() },
                onLogout = { accountViewModel.logout(); navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                onBack = { navController.popBackStack() },
                onNavigateToOrderHistory = { navController.navigate(Screen.OrderHistory.route) },
                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                onToggleDarkMode = { accountViewModel.toggleDarkMode(); onDarkModeChange(!isDarkMode) },
                orderCount = orderHistoryState.orders.size,
                favoritesCount = favoritesState.favorites.size
            )
            }

            composable(Screen.Home.route) {
            HomeScreen(
                onSearch = { street, zip -> storeViewModel.searchStores(street.ifBlank { "1 Main St" }, zip); navController.navigate(Screen.Stores.route) },
                onCartClick = { navController.navigate(Screen.Cart.route) },
                onAccountClick = { navController.navigate(Screen.Account.route) },
                cartItemCount = cartItemCount,
                recentOrderCount = orderHistoryState.orders.size
            )
            }

            composable(Screen.Stores.route) {
                StoresScreen(
                    stores = storeState.stores,
                    onStoreClick = { store -> val id = store.storeID ?: return@StoresScreen; navController.navigate(Screen.Menu.createRoute(id)) },
                    onStoreDetailClick = { store -> val idx = storeState.stores.indexOf(store); if (idx >= 0) navController.navigate(Screen.StoreDetail.createRoute(idx)) },
                    onBack = { navController.popBackStack() }, isLoading = storeState.isLoading
                )
            }

            composable(route = Screen.StoreDetail.route, arguments = listOf(navArgument("storeIndex") { type = NavType.IntType })) { backStackEntry ->
                val storeIndex = backStackEntry.arguments?.getInt("storeIndex") ?: 0
                val store = storeState.stores.getOrNull(storeIndex)
                if (store != null) StoreDetailScreen(store = store, onViewMenu = { val id = store.storeID ?: return@StoreDetailScreen; navController.navigate(Screen.Menu.createRoute(id)) }, onBack = { navController.popBackStack() })
            }

            composable(Screen.Menu.route) { backStackEntry ->
                val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
                LaunchedEffect(storeId) { if (menuState.menuResponse == null || menuState.storeId != storeId) menuViewModel.loadMenu(storeId) }
            MenuScreen(state = menuState, onCategorySelected = { index -> menuViewModel.selectCategory(index) },
                onProductClick = { productCode -> navController.navigate("customize/$storeId/$productCode") },
                onCartClick = { navController.navigate(Screen.Cart.route) }, onBack = { navController.popBackStack() }, cartItemCount = cartItemCount,
                onToggleFavorite = { code, name -> favoritesViewModel.toggleFavorite(code, name) },
                isFavorite = { code -> favoritesViewModel.isFavorite(code) },
                onRetry = { menuViewModel.loadMenu(storeId) },
                onQuickAdd = { code, name, price -> cartViewModel.addItem(CartItem(productCode = code, productName = name, quantity = 1, price = price, id = 0), storeId) })
            }

            composable(route = "customize/{storeId}/{productCode}", arguments = listOf(navArgument("storeId") { type = NavType.StringType }, navArgument("productCode") { type = NavType.StringType })) { backStackEntry ->
                val storeId = backStackEntry.arguments?.getString("storeId") ?: ""; val productCode = backStackEntry.arguments?.getString("productCode") ?: ""
                val item = menuState.selectedCategoryProducts.find { it.productCode == productCode }
                val categoryCode = menuState.flatCategories.getOrNull(menuState.selectedCategoryIndex)?.code
                val fullItem = item?.copy(flavors = menuViewModel.getProductFlavors(productCode, categoryCode), sizes = menuViewModel.getProductSizes(productCode))
                CustomizeProductScreen(storeId = storeId, item = fullItem, onAddToCart = { cartItem, sId -> cartViewModel.addItem(cartItem, sId); navController.popBackStack() }, onBack = { navController.popBackStack() },
                    onToggleFavorite = { code, name -> favoritesViewModel.toggleFavorite(code, name) }, isFavorite = { code -> favoritesViewModel.isFavorite(code) })
            }

            composable(Screen.Cart.route) {
                CartScreen(items = cartState.items, onUpdateQuantity = { id, delta -> cartViewModel.updateQuantity(id, delta) },
                    onRemoveItem = { id -> cartViewModel.removeItem(id) },
                    onCheckout = { val sid = cartState.storeId ?: return@CartScreen; navController.navigate(Screen.Checkout.createRoute(sid)) },
                    onContinueShopping = { navController.popBackStack() },
                    onClearCart = { cartViewModel.clearCart() },
                    storeId = cartState.storeId,
                    storeAddress = storeState.stores.find { it.storeID == cartState.storeId }?.addressDescription,
                    onBack = { navController.popBackStack() })
            }

            composable(route = "checkout/{storeId}", arguments = listOf(navArgument("storeId") { type = NavType.StringType })) { backStackEntry ->
                val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
                CheckoutScreen(storeId = storeId, accountState = accountState, orderState = checkoutState, cartItems = cartState.items,
                    onUpdateField = { field, value -> accountViewModel.updateField(field, value) }, onSaveProfile = { accountViewModel.saveProfile() },
                    onPlaceOrder = { checkoutViewModel.placeOrder(storeId = storeId, customer = accountViewModel.getCustomerForOrder(), cartItems = cartState.items, serviceMethod = checkoutState.serviceMethod, tipAmount = checkoutState.tipAmount); accountViewModel.saveProfile() },
                    onServiceMethodChange = { checkoutViewModel.setServiceMethod(it) },
                    onViewTracking = { orderId ->
                    if (orderId.isNotBlank()) {
                        val total = "%.2f".format(cartViewModel.getSubtotal())
                        val itemsSummary = cartState.items.joinToString(", ") { "${it.quantity}x ${it.productName}" }
                        orderHistoryViewModel.addOrder(orderId, storeId, total, itemsSummary)
                        trackingViewModel.startTracking(storeId, orderId)
                        navController.navigate(Screen.Tracking.createRoute(storeId, orderId))
                    }
                }, onBack = { navController.popBackStack() },
                onTipChange = { checkoutViewModel.setTipAmount(it) })
            }

        composable(route = "tracking/{storeId}/{orderId}", arguments = listOf(navArgument("storeId") { type = NavType.StringType }, navArgument("orderId") { type = NavType.StringType })) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            LaunchedEffect(orderId) { trackingViewModel.startTracking(storeId, orderId) }
            TrackingScreen(orderId = orderId, onBack = { trackingViewModel.stopTracking(); navController.popBackStack() }, currentStage = trackingState.currentStage, isLoadingTracking = trackingState.isLoading)
        }

            composable(Screen.OrderHistory.route) { OrderHistoryScreen(state = orderHistoryState, onBack = { navController.popBackStack() }, onReorder = { storeId -> navController.navigate(Screen.Menu.createRoute(storeId)) }) }
            composable(Screen.Favorites.route) { FavoritesScreen(state = favoritesState, onBack = { navController.popBackStack() }) }
        }
    }
}
