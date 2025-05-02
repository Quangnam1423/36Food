package com.example.a36food

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a36food.presentation.screens.foodscreen.FoodDetailScreen
import com.example.a36food.presentation.screens.restaurantDetail.RestaurantDetailScreen
import com.example.a36food.presentation.screens.homes.FavoriteScreen
import com.example.a36food.presentation.screens.homes.HistoryScreen
import com.example.a36food.presentation.screens.homes.HomeScreen
import com.example.a36food.presentation.screens.homes.ProfileScreen
import com.example.a36food.presentation.screens.homes.SearchingScreen
import com.example.a36food.presentation.screens.introduce.IntroduceScreen
import com.example.a36food.presentation.screens.login.LoginScreen

sealed class Screen(val route: String) {
    data object Introduce : Screen("introduce")
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Favorite : Screen("favorite")
    data object History : Screen("history")
    data object Profile : Screen("profile")
    data object RestaurantDetail : Screen("restaurant_detail/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant_detail/$restaurantId"
    }
    data object FoodDetail : Screen("food_detail/{foodId}") {
        fun createRoute(foodId: String) = "food_detail/$foodId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Introduce.route
    ) {
        composable(Screen.Introduce.route) {
            IntroduceScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Introduce.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Introduce.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    // Add register navigation when ready
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToFavorite = { navController.navigate(Screen.Favorite.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onRestaurantClick = { restaurantId ->
                    navController.navigate(Screen.RestaurantDetail.createRoute(restaurantId))
                }
            )
        }

        composable(Screen.Search.route) {
            SearchingScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToSearch = { /* Already on search */ },
                onNavigateToFavorite = { navController.navigate(Screen.Favorite.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Favorite.route) {
             FavoriteScreen(
                 onNavigateToHome = { navController.navigate(Screen.Home.route) },
                 onNavigateToHistory = { navController.navigate(Screen.History.route) },
                 onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                 onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                 onRestaurantClick = { restaurantId ->
                     navController.navigate(Screen.RestaurantDetail.createRoute(restaurantId))
                 }
             )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToFavorite = { navController.navigate(Screen.Favorite.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToFavorite = { navController.navigate(Screen.Favorite.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onEditClick = { /* TODO: Implement edit profile */ },
                onPasswordChange = { /* TODO: Implement password change */ },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        // navController.navigate(Screen.RestaurantDetail.createRoute
        composable(
            route = Screen.RestaurantDetail.route,
            arguments = listOf(
                navArgument("restaurantId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId")
            requireNotNull(restaurantId) { "Restaurant ID is required" }

            RestaurantDetailScreen(
                restaurantId = restaurantId,
                onBackClick = { navController.popBackStack() },
                onShareClick = { /* TODO: Implement share functionality */ },
                onAddClick = {/*TO DO ADD FOOD TO CART*/},
                onFoodClick = { foodId ->
                    navController.navigate(Screen.FoodDetail.createRoute(foodId))
                }
            )
        }

        composable(
            route = Screen.FoodDetail.route,
            arguments = listOf(
                navArgument("foodId") { type = NavType.StringType }
            )
        ) {
            FoodDetailScreen(
                foodId = it.arguments?.getString("foodId") ?: "",
                onBackClick = { navController.popBackStack() },
                onAddToCartClick = {},
                onShareClick = {},
            )
        }
    }
}