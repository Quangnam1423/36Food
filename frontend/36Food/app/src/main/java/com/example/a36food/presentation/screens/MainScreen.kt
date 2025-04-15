package com.example.a36food.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.a36food.ui.theme._36FoodTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.a36food.R

import com.example.a36food.ui.components.BottomNavBar
import com.example.a36food.ui.components.HomeTopAppBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainMenuScreen() {
    Scaffold (
        topBar = {
            HomeTopAppBar()
        },
        bottomBar = {
            BottomNavBar()
        }
    ) { paddingValues ->
        MenuLayout(Modifier.padding(paddingValues))
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
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Danh Mục",
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFFFF5722)
            )

            Text(
                "Tất cả >",
                color = Color.Gray,
                modifier = Modifier.clickable { /* TODO */ }
            )
        }
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
fun SuggestionRestaurants (

) {
    //val restaurants = listOf (
       // Restaurant("Nhà Hàng Bình Thái", 3.6, "18%", "36p", R.drawable.res1)

    //)
}