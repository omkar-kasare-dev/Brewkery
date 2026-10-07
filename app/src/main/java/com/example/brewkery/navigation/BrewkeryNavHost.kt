package com.example.brewkery.navigation


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
            // TEMP placeholder: replaced by MenuScreen
            Placeholder("Menu") {
                PlaceholderButton("Open item 1") {
                    navController.navigate(DetailRoute(itemId = 1)) { launchSingleTop = true }
                }
                PlaceholderButton("View cart") {
                    navController.navigate(CartRoute) { launchSingleTop = true }
                }
                PlaceholderButton("Active order card") {
                    navController.navigate(OrderRoute) { launchSingleTop = true }
                }
            }
        }

        composable<DetailRoute> { entry ->
            val route = entry.toRoute<DetailRoute>()
            // TEMP placeholder: replaced by DetailScreen
            Placeholder("Detail (item ${route.itemId})") {
                PlaceholderButton("Add to cart") {
                    // Brief's flow: Add to Cart -> Cart. Pop Detail so Back returns to Menu.
                    navController.navigate(CartRoute) {
                        popUpTo<DetailRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                }
                PlaceholderButton("Back") { navController.popBackStack() }
            }
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