package com.example.a36food.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a36food.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import com.example.a36food.domain.model.FoodItem
import com.example.a36food.ui.components.SearchResultAppBar
import androidx.compose.material3.Card
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.a36food.domain.model.Restaurant

@Composable
fun SearchResultScreen() {
    Scaffold(
        topBar = {
            SearchResultAppBar(
                query = "Bun Cha",
                onKeywordChanged = {}
            )
        },
    ) { paddingValues ->
            SearchResultLayout(modifier = Modifier.padding(paddingValues))
    }
}


@Composable
fun SearchResultLayout(
    modifier: Modifier = Modifier,
) {
    val sampleRestaurant = Restaurant(
        name = "Nhà hàng ABC",
        imageRes = R.drawable.restaurant, // thay bằng resource thật nếu có
        rating = 4.5f,
        address = "123 Đường ABC, TP.HCM",
        priceRange = "$$",
        isOpen = true
    )

    val sampleFoodItemList = listOf(
        FoodItem(
            id = 1,
            name = "Pizza Hải Sản",
            price = 10000.0,
            restaurant = sampleRestaurant,
            imageResId = R.drawable.pizza, // thay bằng resource thật nếu có
            isAvailable = true,
            isPopular = true,
            like = 10,
            saleCount = 400
        ),
        FoodItem(
            id = 2,
            name = "Sushi Tươi",
            price = 12000.0,
            restaurant = sampleRestaurant,
            imageResId = R.drawable.pizza, // thay bằng resource thật nếu có
            isAvailable = true,
            isPopular = true,
            like = 25,
            saleCount = 350
        ),
        FoodItem(
            id = 3,
            name = "Mỳ Ý Carbonara",
            price = 15000.0,
            restaurant = sampleRestaurant,
            imageResId = R.drawable.pho, // thay bằng resource thật nếu có
            isAvailable = true,
            isPopular = false,
            like = 5,
            saleCount = 150
        ),
        FoodItem(
            id = 4,
            name = "Burger Phô Mai",
            price = 8000.0,
            restaurant = sampleRestaurant,
            imageResId = R.drawable.burger, // thay bằng resource thật nếu có
            isAvailable = false,
            isPopular = false,
            like = 3,
            saleCount = 50
        ),
        FoodItem(
            id = 5,
            name = "Salad Rau Củ",
            price = 6000.0,
            restaurant = sampleRestaurant,
            imageResId = R.drawable.tom_hum_suggestion, // thay bằng resource thật nếu có
            isAvailable = true,
            isPopular = true,
            like = 30,
            saleCount = 200
        ),
        FoodItem(
            id = 6,
            name = "Cơm Gà Xối Mỡ",
            price = 9000.0,
            restaurant = sampleRestaurant,
            imageResId = R.drawable.rice, // thay bằng resource thật nếu có
            isAvailable = true,
            isPopular = true,
            like = 50,
            saleCount = 500
        ),
        FoodItem(
            id = 6,
            name = "Cơm Gà Xối Mỡ",
            price = 9000.0,
            restaurant = sampleRestaurant,
            imageResId = R.drawable.rice, // thay bằng resource thật nếu có
            isAvailable = true,
            isPopular = true,
            like = 50,
            saleCount = 500
        ),
        FoodItem(
            id = 6,
            name = "Cơm Gà Xối Mỡ",
            price = 9000.0,
            restaurant = sampleRestaurant,
            imageResId = R.drawable.rice, // thay bằng resource thật nếu có
            isAvailable = true,
            isPopular = true,
            like = 50,
            saleCount = 500
        ),
        FoodItem(
            id = 6,
            name = "Cơm Gà Xối Mỡ",
            price = 9000.0,
            restaurant = sampleRestaurant,
            imageResId = R.drawable.rice, // thay bằng resource thật nếu có
            isAvailable = true,
            isPopular = true,
            like = 50,
            saleCount = 500
        ),
        FoodItem(
            id = 6,
            name = "Cơm Gà Xối Mỡ",
            price = 9000.0,
            restaurant = sampleRestaurant,
            imageResId = R.drawable.rice, // thay bằng resource thật nếu có
            isAvailable = true,
            isPopular = true,
            like = 50,
            saleCount = 500
        )
    )
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // Add padding if needed
    ) {
        items(sampleFoodItemList) { item ->
            FoodItemCard(
                modifier = Modifier.padding(vertical = 8.dp),
                item = item,
                onAddClick = {}
            )
        }
    }
}

@Composable
fun FoodItemCard(
    modifier: Modifier = Modifier,
    item: FoodItem,
    onAddClick:(FoodItem) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val cardWidth = screenWidth - 16.dp
    val cardHeight = screenHeight / 3

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Image(
                painter = painterResource(item.imageResId),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.saleCount} đã bán",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "  |  ${item.like} lượt thích",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${item.price.toInt()}đ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { onAddClick(item) },
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .background(Color(0xFFFF5722), shape = CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultScreenPreview() {
    SearchResultScreen()
}

