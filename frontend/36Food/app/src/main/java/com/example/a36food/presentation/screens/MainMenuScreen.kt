package com.example.a36food.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.a36food.ui.theme._36FoodTheme
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.a36food.R
import com.example.a36food.ui.components.RoundedIconButton


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainMenuScreen() {
    Scaffold (
        topBar = {
            MenuAppBar()
        },
        bottomBar = {
            BottomNavBar()
        }
    ) { paddingValues ->
        MenuLayout(Modifier.padding(paddingValues))
    }
}

@Composable
fun MenuAppBar() {
    TopAppBar(
        backgroundColor = colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoundedIconButton(
                onClick = {/*TO DO*/},
                icon = Icons.Default.Menu,
                contentDescription = "Menu"
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))

            Column(
            ){
                Text("Giao hàng đến",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Green
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                Text("Yên Xá, Tân Triều, Thanh Trì...",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFF5722),
                    maxLines = 1,
                    modifier = Modifier
                        .clickable { /* TO DO*/ }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { /* open search */ }
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun MenuLayout(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Chúc bạn ngon miệng, Bình!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        CategoryGrid(onCategoryClick = {})
    }
}

@Composable
fun CategoryGrid (
    onCategoryClick: (String) -> Unit
) {
    val categories = listOf (
        "Com" to R.drawable.rice,
        "Phở" to R.drawable.pho,
        "Pizza" to R.drawable.pizza,
        "Burger" to R.drawable.burger,
        "Đồ ăn nhanh" to R.drawable.fast_food,
        "Đồ uống" to R.drawable.drink
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.height(200.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(categories) { (name, imageRes) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp).clickable { onCategoryClick(name) }
            ) {
                Image(
                    painterResource(imageRes),
                    contentDescription = name,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(name, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun BottomNavBar(

) {

    val homeSelected = remember {mutableStateOf(false)}
    val favoriteSelected = remember { mutableStateOf(false)}
    val historySelected = remember { mutableStateOf(false)}
    val personalSelected = remember {mutableStateOf(false)}

    NavigationBar (
        modifier = Modifier.background(Color.Green)
    ) {
        NavigationBarItem (
            icon = {Icon(Icons.Default.Home,  contentDescription = "Home")},
            label = {Text("Home")},
            selected = homeSelected.value,
            onClick = {
                homeSelected.value = true
                favoriteSelected.value = false
                historySelected.value = false
                personalSelected.value = false
            }
        )
        NavigationBarItem (
            icon = {Icon(Icons.Filled.Favorite, contentDescription = "Yêu Thích")},
            label = {Text("Favorite")},
            selected = favoriteSelected.value,
            onClick = {
                favoriteSelected.value = true
                homeSelected.value = false
                historySelected.value = false
                personalSelected.value = false
            }
        )
        NavigationBarItem (
            icon = {Icon(Icons.Default.History,  contentDescription = "History")},
            label = {Text("History")},
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
            icon = {Icon(Icons.Filled.Person,  contentDescription = "Profile")},
            label = {Text("Profile")},
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

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    _36FoodTheme {
        MainMenuScreen()
    }
}

@Preview
@Composable
fun CategoryGridPreview() {
    CategoryGrid(
        onCategoryClick = {}
    )
}