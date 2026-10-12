package com.example.brewkery.navigation
/*
import com.example.brewkery.presentation.detail.DetailScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.brewkery.presentation.menu.MenuScreen
import kotlinx.serialization.Serializable

@Serializable
data object MenuRoute

@Serializable
data class DetailRoute(val itemId: Int)

@Serializable
data object CartRoute

@Serializable
data object OrderRoute

@Composable
fun BrewkeryNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = MenuRoute,
        modifier = modifier
    ) {
        composable<MenuRoute> {
            MenuScreen(
                onItemClick = { itemId ->
                    navController.navigate(DetailRoute(itemId)) { launchSingleTop = true }
                },
                onCartClick = {
                    navController.navigate(CartRoute) { launchSingleTop = true }
                },
                onOrderClick = {
                    navController.navigate(OrderRoute) { launchSingleTop = true }
                }
            )
        }

        composable<DetailRoute> {
            DetailScreen(
                onBack = { navController.popBackStack() },
                onAddedToCart = {
                    // Brief's flow: Add to Cart -> Cart. Pop Detail so Back from Cart returns to Menu.
                    navController.navigate(CartRoute) {
                        popUpTo<DetailRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<CartRoute> {
            // TEMP placeholder: replaced by CartScreen
            Placeholder("Cart") {
                PlaceholderButton("Place order") {
                    // Drop Cart from the stack so Back from Order returns to Menu.
                    navController.navigate(OrderRoute) {
                        popUpTo<MenuRoute>()
                        launchSingleTop = true
                    }
                }
                PlaceholderButton("Back") { navController.popBackStack() }
            }
        }

        composable<OrderRoute> {
            // TEMP placeholder: replaced by OrderScreen
            Placeholder("Order placed") {
                PlaceholderButton("Back to Menu") {
                    navController.popBackStack<MenuRoute>(inclusive = false)
                }
            }
        }
    }
}

@Composable
private fun Placeholder(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title)
        content()
    }
}

@Composable
private fun PlaceholderButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick) { Text(label) }
}

 */



import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.brewkery.presentation.cart.CartScreen
import com.example.brewkery.presentation.detail.DetailScreen
import com.example.brewkery.presentation.menu.MenuScreen
import com.example.brewkery.presentation.order.OrderScreen
import kotlinx.serialization.Serializable

@Serializable
data object MenuRoute

@Serializable
data class DetailRoute(val itemId: Int)

@Serializable
data object CartRoute

@Serializable
data object OrderRoute

@Composable
fun BrewkeryNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = MenuRoute,
        modifier = modifier
    ) {
        composable<MenuRoute> {
            MenuScreen(
                onItemClick = { itemId ->
                    navController.navigate(DetailRoute(itemId)) { launchSingleTop = true }
                },
                onCartClick = {
                    navController.navigate(CartRoute) { launchSingleTop = true }
                },
                onOrderClick = {
                    navController.navigate(OrderRoute) { launchSingleTop = true }
                }
            )
        }

        composable<DetailRoute> {
            DetailScreen(
                onBack = { navController.popBackStackSafely() },
                onAddedToCart = {
                    // Brief's flow: Add to Cart -> Cart. Pop Detail so Back from Cart returns to Menu.
                    navController.navigate(CartRoute) {
                        popUpTo<DetailRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<CartRoute> {
            CartScreen(
                onBack = { navController.popBackStackSafely() },
                onOrderPlaced = {
                    // Drop Cart from the stack so Back from Order returns to Menu.
                    navController.navigate(OrderRoute) {
                        popUpTo<MenuRoute>()
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<OrderRoute> {
            OrderScreen(
                onBackToMenu = { navController.popBackStack<MenuRoute>(inclusive = false) }
            )
        }
    }
}

/**
 * A second tap on Back while the first pop is still animating would pop the Menu too and leave a
 * blank screen. Only pop when there is something to go back to.
 */
private fun NavHostController.popBackStackSafely() {
    if (previousBackStackEntry != null) popBackStack()
}