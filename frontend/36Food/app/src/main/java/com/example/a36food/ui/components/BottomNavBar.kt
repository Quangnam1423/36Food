package com.example.a36food.ui.components

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BottomNavBar(

) {

    val homeSelected = remember { mutableStateOf(false) }
    val favoriteSelected = remember { mutableStateOf(false) }
    val historySelected = remember { mutableStateOf(false) }
    val personalSelected = remember { mutableStateOf(false) }

    NavigationBar (
        modifier = Modifier.background(Color.Green)
    ) {
        NavigationBarItem (
            icon = { Icon(Icons.Default.Home,  contentDescription = "Home") },
            label = { Text("Home") },
            selected = homeSelected.value,
            onClick = {
                homeSelected.value = true
                favoriteSelected.value = false
                historySelected.value = false
                personalSelected.value = false
            }
        )
        NavigationBarItem (
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Yêu Thích") },
            label = { Text("Favorite") },
            selected = favoriteSelected.value,
            onClick = {
                favoriteSelected.value = true
                homeSelected.value = false
                historySelected.value = false
                personalSelected.value = false
            }
        )
        NavigationBarItem (
            icon = { Icon(Icons.Default.History,  contentDescription = "History") },
            label = { Text("History") },
            selected = historySelected.value,
            onClick = {
                /*TO DO*/
                favoriteSelected.value = false
                homeSelected.value = false
                historySelected.value = true
                personalSelected.value = false
            }
        )
        NavigationBarItem (
            icon = { Icon(Icons.Filled.Person,  contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = personalSelected.value,
            onClick = {
                /*TO DO*/
                favoriteSelected.value = false
                homeSelected.value = false
                historySelected.value = false
                personalSelected.value = true
            }
        )
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    BottomNavBar()
}