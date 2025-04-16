package com.example.a36food.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a36food.R
import com.example.a36food.data.local.RecentSearchManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.a36food.domain.model.FoodItem
import com.example.a36food.ui.components.BottomNavBar
import com.example.a36food.ui.components.SearchResultAppBar
import com.example.a36food.ui.components.SearchingTopAppBar
import androidx.compose.material3.Card
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.ui.components.RoundedIconButton

@Composable
fun SearchResultScreen() {
    Scaffold(
        topBar = {
            SearchResultAppBar(
                query = "Bun Cha",
                onKeywordChanged = {}
            )
        }
    ) { paddingValues ->
            SearchResultLayout(modifier = Modifier.padding(paddingValues))

    }
}

@Composable
fun SearchResultLayout(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

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
        /*
        Box(modifier = Modifier.fillMaxSize()) {
            Row (modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(item.imageResId),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(cardWidth / 3)
                        .height(cardHeight)
                        .padding(8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.name,
                        color = Color.Green,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    item.restaurant?.let {
                        Text(text = it.name, color = Color.Green)
                    }
                }
            }

            IconButton(
                onClick = { onAddClick(item) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(color = Color(0xFFFF9800), shape = CircleShape)
                    .size(28.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "add FoodItem",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

         */
    }
}

@Preview
@Composable
fun FoodItemCardPreview() {
    val sampleRestaurant = Restaurant(
        name = "Nhà hàng ABC",
        imageRes = R.drawable.restaurant, // thay bằng resource thật nếu có
        rating = 4.5f,
        address = "123 Đường ABC, TP.HCM",
        priceRange = "$$",
        isOpen = true
    )

    val sampleFoodItem = FoodItem(
        id = 1,
        name = "Pizza Hải Sản",
        price = 10000.0,
        restaurant = sampleRestaurant,
        imageResId = R.drawable.pizza, // thay bằng resource thật nếu có
        isAvailable = true,
        isPopular = true,
        like = 10,
        saleCount = 400
    )
    FoodItemCard(
        modifier = Modifier,
        item = sampleFoodItem,
        onAddClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SearchResultScreenPreview() {
    SearchResultScreen()
}

