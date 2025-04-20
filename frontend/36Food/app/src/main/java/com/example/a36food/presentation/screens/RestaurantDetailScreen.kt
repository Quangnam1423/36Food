package com.example.a36food.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a36food.R
import com.example.a36food.domain.model.FoodItem
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.ui.components.RoundedIconButton
import androidx.compose.material3.Text as Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen (

) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            RestaurantDetailAppBar(
                restaurantName = "Restaurant Name",
                query = "",
                onQueryChanged = {},
                onBackClick = {},
                onShareClick = {},
                scrollBehavior = scrollBehavior
            )

        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        RestaurantDetailLayout(Modifier.padding(paddingValues))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  RestaurantDetailAppBar(
    restaurantName: String,
    query: String,
    onQueryChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    CenterAlignedTopAppBar(
        title = {
            OutlinedTextField(
                value = query,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.padding(
                    horizontal = 2.dp, vertical = 2.dp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = { onQueryChanged(it) },
                trailingIcon = {
                    IconButton(
                        onClick = {/*TO DO*/ }
                    ) {
                        Icon(imageVector = Icons.Default.Cancel, contentDescription = "hidden Icon")
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {/*TO DO*/ }
                )
            )
        },
        navigationIcon = {
            RoundedIconButton(
                onClick = onBackClick,
                icon = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back"
            )
        },
        actions = {
            RoundedIconButton(
                onClick = onShareClick,
                icon = Icons.Default.Share,
                contentDescription = "Share"
            )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun RestaurantDetailLayout(
    modifier: Modifier = Modifier
) {
    val sampleRestaurant = Restaurant(
        id = 1,
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
        modifier = Modifier.fillMaxSize()
    ) {
        item{
            RestaurantDetailHeader(
                restaurant = sampleRestaurant,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            Text(text = "Danh sách các món ăn của nhà hàng", modifier = Modifier.padding(4.dp))
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(sampleFoodItemList) { foodItem ->
            FoodItemCard(
                item = foodItem,
                modifier = Modifier.fillMaxWidth(),
                onAddClick = {}
            )
        }
    }
}

@Composable
fun RestaurantDetailHeader(
    restaurant: Restaurant,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(restaurant.imageRes),
        contentDescription = "restaurant banner",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )

    Row(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFA500))
                Text(text = restaurant.rating.toString(), fontWeight = FontWeight.Bold)
                Text(" |  10+ bình luận  |  29 phút", modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {}
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = "add favorite restaurant", tint = Color.Red)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RestaurantDetailScreenPreview() {
    RestaurantDetailScreen()
}