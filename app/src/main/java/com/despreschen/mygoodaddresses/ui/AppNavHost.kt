package com.despreschen.mygoodaddresses.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.despreschen.mygoodaddresses.ui.add.AddRestaurantScreen
import com.despreschen.mygoodaddresses.ui.list.RestaurantListScreen
import com.despreschen.mygoodaddresses.ui.map.RestaurantMapScreen

private object Routes {
    const val LIST = "restaurants"
    const val ADD = "restaurants/add"
    const val MAP = "restaurants/{restaurantId}/map"

    fun map(restaurantId: Long) = "restaurants/$restaurantId/map"
}

private const val RESTAURANT_ID = "restaurantId"

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LIST,
        modifier = modifier,
    ) {
        composable(Routes.LIST) {
            RestaurantListScreen(
                onAddRestaurant = { navController.navigate(Routes.ADD) },
                onOpenMap = { id -> navController.navigate(Routes.map(id)) },
            )
        }
        composable(Routes.ADD) {
            AddRestaurantScreen(
                onNavigateBack = navController::popBackStack,
                // The list is backed by the database, so returning to it is
                // enough for the new row to appear.
                onSaved = navController::popBackStack,
            )
        }
        composable(
            route = Routes.MAP,
            arguments = listOf(navArgument(RESTAURANT_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            RestaurantMapScreen(
                restaurantId = backStackEntry.arguments?.getLong(RESTAURANT_ID) ?: 0L,
                onNavigateBack = navController::popBackStack,
            )
        }
    }
}
