import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a36food.presentation.screens.restaurantDetail.RestaurantDetailScreen
import com.example.a36food.presentation.screens.homes.FavoriteScreen
import com.example.a36food.presentation.screens.homes.HomeScreen
import com.example.a36food.presentation.screens.homes.SearchingScreen
import com.example.a36food.presentation.screens.introduce.IntroduceScreen
import com.example.a36food.presentation.screens.login.LoginScreen

sealed class Screen(val route: String) {
    object Introduce : Screen("introduce")
    object Login : Screen("login")
    object Home : Screen("home")
    object Search : Screen("search")
    object Favorite : Screen("favorite")
    object History : Screen("history")
    object Profile : Screen("profile")
    object RestaurantDetail : Screen("restaurant_detail/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant_detail/$restaurantId"
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
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
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
            Box(modifier = Modifier.fillMaxSize()) {
                Text("History Screen", modifier = Modifier.align(Alignment.Center))
            }
        }

        composable(Screen.Profile.route) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("Profile Screen", modifier = Modifier.align(Alignment.Center))
            }
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
                onShareClick = { /* TODO: Implement share functionality */ }
            )
        }
    }
}