package com.example.a36food.ui.components

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BottomNavBar(
    selectedRoute: String = Screen.Home.route,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val navItems = listOf(
        NavItem(
            route = Screen.Home.route,
            icon = Icons.Default.Home,
            label = "Home",
            onClick = onNavigateToHome
        ),
        NavItem(
            route = Screen.Search.route,
            icon = Icons.Default.Search,
            label = "Search",
            onClick = onNavigateToSearch
        ),
        NavItem(
            route = Screen.Favorite.route,
            icon = Icons.Filled.Favorite,
            label = "Favorite",
            onClick = onNavigateToFavorite
        ),
        NavItem(
            route = Screen.History.route,
            icon = Icons.Default.History,
            label = "History",
            onClick = onNavigateToHistory
        ),
        NavItem(
            route = Screen.Profile.route,
            icon = Icons.Filled.Person,
            label = "Profile",
            onClick = onNavigateToProfile
        )
    )

    NavigationBar(
        modifier = Modifier.background(Color.White),
        containerColor = Color.White
    ) {
        navItems.forEach { item ->
            val isSelected = selectedRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Color(0xFFFF5722) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Color(0xFFFF5722) else Color.Gray
                    )
                },
                selected = isSelected,
                onClick = item.onClick,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF5722),
                    selectedTextColor = Color(0xFFFF5722),
                    indicatorColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

private data class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)