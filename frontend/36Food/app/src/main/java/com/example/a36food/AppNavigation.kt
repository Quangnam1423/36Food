package com.example.a36food

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a36food.domain.model.LocationData
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a36food.presentation.screens.ChangePasswordScreen
import com.example.a36food.presentation.screens.NoConnectionScreen
import com.example.a36food.presentation.screens.foodscreen.FoodDetailScreen
import com.example.a36food.presentation.screens.restaurantDetail.RestaurantDetailScreen
import com.example.a36food.presentation.screens.homes.FavoriteScreen
import com.example.a36food.presentation.screens.homes.HistoryScreen
import com.example.a36food.presentation.screens.homes.HomeScreen
import com.example.a36food.presentation.screens.homes.ProfileScreen
import com.example.a36food.presentation.screens.homes.SearchingScreen
import com.example.a36food.presentation.screens.introduce.IntroduceScreen
import com.example.a36food.presentation.screens.login.LoginScreen
import com.example.a36food.presentation.screens.register.RegisterScreen
import com.example.a36food.presentation.viewmodel.NetworkViewModel
import com.example.a36food.presentation.viewmodel.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

sealed class Screen(val route: String) {
    data object Introduce : Screen("introduce")
    data object NoConnection : Screen("no_connection")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Favorite : Screen("favorite")
    data object ChangePassword: Screen("change_password")
    data object History : Screen("history")
    data object Profile : Screen("profile")
    data object RestaurantDetail : Screen("restaurant_detail/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant_detail/$restaurantId"
    }
    data object FoodDetail : Screen("food_detail/{foodId}") {
        fun createRoute(foodId: String) = "food_detail/$foodId"
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val locationViewModel: LocationViewModel = hiltViewModel()
    val locationData by locationViewModel.locationData.collectAsStateWithLifecycle()

    LocationPermissionCheck(
        locationViewModel = locationViewModel,
        onPermissionGranted = {
            Log.d("AppNavigation", "Location permission granted")
            // You can use the location data here
            val currentLocation: LocationData? = locationData
            Log.d("AppNavigation", "Current location: $currentLocation")

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

                composable(Screen.NoConnection.route) {
                    val networkViewModel: NetworkViewModel = hiltViewModel()

                    NoConnectionScreen(
                        onRetry = {
                            // Check connection and navigate back if connection is restored
                            if (networkViewModel.isNetworkAvailable()) {
                                // Navigate back to previous screen or home screen
                                navController.popBackStack()
                            }
                        },
                        onExit = {
                            // Get current activity and finish it to exit the app
                            val activity = context.findActivity()
                            activity?.finish()
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
                            navController.navigate(Screen.Register.route)
                        },
                        onNetworkError = {
                            navController.navigate(Screen.NoConnection.route)
                        }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onLoginClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        },
                        onNavigateToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        },
                        onNetworkError = {
                            navController.navigate(Screen.NoConnection.route)
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
                        },
                        onNetworkError = {
                            navController.navigate(Screen.NoConnection.route)
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
                        onPasswordChange = {navController.navigate(Screen.ChangePassword.route)},
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Profile.route) { inclusive = true }
                            }
                        },
                        onNetworkError = {
                            navController.navigate(Screen.NoConnection.route)
                        }
                    )
                }

                composable(Screen.ChangePassword.route) {
                    ChangePasswordScreen(
                        onNavigateBack = { navController.popBackStack()},
                        onNetworkError = {
                            navController.navigate(Screen.NoConnection.route)
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
    )
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionCheck(
    locationViewModel: LocationViewModel = hiltViewModel(),
    onPermissionGranted: @Composable () -> Unit
) {
    val context = LocalContext.current
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val isLocationEnabled by locationViewModel.isLocationEnabled.collectAsStateWithLifecycle()
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showLocationSettingsDialog by remember { mutableStateOf(false) }

    // Track if we've sent the user to settings
    var sentToLocationSettings by remember { mutableStateOf(false) }

    // Check location settings every time the app regains focus
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                locationViewModel.checkLocationSettings()

                // If we sent user to settings and location is now enabled, close dialog
                if (sentToLocationSettings && locationViewModel.isLocationEnabled.value) {
                    showLocationSettingsDialog = false
                    sentToLocationSettings = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted, isLocationEnabled) {
        if (locationPermissionsState.allPermissionsGranted) {
            locationViewModel.checkLocationSettings()
            if (isLocationEnabled) {
                locationViewModel.fetchCurrentLocation()
                showLocationSettingsDialog = false
            } else {
                showLocationSettingsDialog = true
            }
        } else {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    // Always show the app content
    Box(modifier = Modifier.fillMaxSize()) {
        // Main app content
        onPermissionGranted()

        // Show permission dialog when needed
        if (!locationPermissionsState.allPermissionsGranted &&
            (showPermissionDialog || locationPermissionsState.shouldShowRationale)) {
            PermissionDialog(
                onDismiss = { showPermissionDialog = false },
                onOkClick = { locationPermissionsState.launchMultiplePermissionRequest() },
                onSettingsClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            )
        }

        // Show location settings dialog when needed
        if (locationPermissionsState.allPermissionsGranted && !isLocationEnabled && showLocationSettingsDialog) {
            LocationSettingsDialog(
                onDismiss = { showLocationSettingsDialog = false },
                onEnableClick = {
                    sentToLocationSettings = true
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun LocationSettingsDialog(
    onDismiss: () -> Unit,
    onEnableClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dịch vụ vị trí đang tắt") },
        text = {
            Text("Để sử dụng tính năng vị trí, bạn cần bật dịch vụ vị trí trên thiết bị.")
        },
        confirmButton = {
            Button(onClick = onEnableClick) {
                Text("Bật vị trí")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Để sau")
            }
        }
    )
}

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quyền truy cập vị trí") },
        text = {
            Text("Để sử dụng đầy đủ tính năng của ứng dụng, chúng tôi cần quyền truy cập vị trí của bạn. Bạn có thể cấp quyền ngay bây giờ hoặc vào cài đặt để cấp quyền sau.")
        },
        confirmButton = {
            Button(onClick = onOkClick) {
                Text("Cấp quyền ngay")
            }
        },
        dismissButton = {
            Button(onClick = onSettingsClick) {
                Text("Vào cài đặt")
            }
        }
    )
}