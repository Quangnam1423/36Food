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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a36food.R
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.ui.components.BottomNavBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import com.example.a36food.ui.components.RoundedIconButton
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.a36food.ui.components.CartIcon
import com.example.a36food.ui.components.MessageIcon


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen() {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val lazyListState = rememberLazyListState()


    Scaffold (
        topBar = {
            HomeTopAppBar(
                location = "ngách 120 Ng. 245 P.Định Công, Định Công, Quận Hoàng Mai, Hà Nội",
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomNavBar()
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        MenuLayout(Modifier.padding(paddingValues))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    location: String,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Column(
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                   Icon(
                       Icons.Default.LocationOn,
                       contentDescription = "location"
                   )
                    Text(
                        text = location,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "fix location"

                    )
                }
            }
        },
        actions = {
            CartIcon(
                cartCount = 3,
                onClick = {}
            )
            IconButton(
                onClick = {}
            ) {
                MessageIcon(
                    messageCount = 3,
                    onClick = {}
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFF5722), // Màu đỏ cam ShopeeFood
            titleContentColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text("Tìm món hoặc quán")
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .height(40.dp)
            .padding(end = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}



@Composable
fun MenuLayout(
    modifier: Modifier = Modifier
) {

    val restaurantList = listOf(
        Restaurant(
            name = "Nhà hàng ABC",
            imageRes = R.drawable.restaurant, // thay bằng resource thật nếu có
            rating = 4.5f,
            address = "123 Đường ABC, TP.HCM",
            priceRange = "$$",
            isOpen = true
        ),
        Restaurant(
            name = "Nhà hàng ABC",
            imageRes = R.drawable.restaurant, // thay bằng resource thật nếu có
            rating = 4.5f,
            address = "123 Đường ABC, TP.HCM",
            priceRange = "$$",
            isOpen = true
        ),
        Restaurant(
            name = "Nhà hàng ABC",
            imageRes = R.drawable.restaurant, // thay bằng resource thật nếu có
            rating = 4.5f,
            address = "123 Đường ABC, TP.HCM",
            priceRange = "$$",
            isOpen = true
        ),
        Restaurant(
            name = "Nhà hàng ABC",
            imageRes = R.drawable.restaurant, // thay bằng resource thật nếu có
            rating = 4.5f,
            address = "123 Đường ABC, TP.HCM",
            priceRange = "$$",
            isOpen = true
        ),
        Restaurant(
            name = "Nhà hàng ABC",
            imageRes = R.drawable.restaurant, // thay bằng resource thật nếu có
            rating = 4.5f,
            address = "123 Đường ABC, TP.HCM",
            priceRange = "$$",
            isOpen = true
        ),
        Restaurant(
            name = "Nhà hàng ABC",
            imageRes = R.drawable.restaurant, // thay bằng resource thật nếu có
            rating = 4.5f,
            address = "123 Đường ABC, TP.HCM",
            priceRange = "$$",
            isOpen = true
        ),
        Restaurant(
            name = "Nhà hàng ABC",
            imageRes = R.drawable.restaurant, // thay bằng resource thật nếu có
            rating = 4.5f,
            address = "123 Đường ABC, TP.HCM",
            priceRange = "$$",
            isOpen = true
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chúc Bạn Ngon Miệng, Bình!",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFF9800),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
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

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            restaurantList.forEach { it ->
                RestaurantCard(
                    restaurant = it,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


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
fun RestaurantCard(
    restaurant: Restaurant,
    modifier: Modifier = Modifier
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
                painter = painterResource(restaurant.imageRes),
                contentDescription = "Restaurant Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Star",
                        tint = Color(0xFFFFA500)
                    )
                    Text(
                        text = restaurant.rating.toString(),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "  |  km",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        text = "  |  phút",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

@Preview
@Composable
fun RestaurantCardPreview() {
    val sampleRestaurant = Restaurant(
        name = "Nhà hàng ABC",
        imageRes = R.drawable.restaurant, // thay bằng resource thật nếu có
        rating = 4.5f,
        address = "123 Đường ABC, TP.HCM",
        priceRange = "$$",
        isOpen = true
    )
    RestaurantCard(sampleRestaurant, modifier = Modifier.fillMaxWidth())
}