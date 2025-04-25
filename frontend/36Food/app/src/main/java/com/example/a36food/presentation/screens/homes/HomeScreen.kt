package com.example.a36food.presentation.screens.homes

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a36food.R
import com.example.a36food.domain.model.BusinessHours
import com.example.a36food.domain.model.OpeningStatus
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.domain.model.ServiceType
import com.example.a36food.ui.components.BottomNavBar
import com.example.a36food.ui.components.CartIcon
import com.example.a36food.ui.components.MessageIcon

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit = {},
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {

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
            BottomNavBar(
                selectedRoute = Screen.Home.route,
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToFavorite = onNavigateToFavorite,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToProfile = onNavigateToProfile
            )
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
            id = "1",
            name = "Phở Thìn Bờ Hồ",
            imageUrl = "https://example.com/pho.jpg", // tạm thời dùng R.drawable khi hiển thị
            rating = 4.5f,
            ratingCount = 234,
            address = "13 Lò Đúc, Hai Bà Trưng, Hà Nội",
            priceRange = "20000",
            openingStatus = OpeningStatus.SCHEDULED,
            businessHours = BusinessHours(
                openTime = "07:00",
                closeTime = "22:00"
            ),
            serviceType = ServiceType.FOOD,
            phoneNumber = "0123456789",
            likes = 156,
            categories = listOf("Phở", "Món Việt", "Đặc sản")
        ),
        Restaurant(
            id = "2",
            name = "Pizza 4P's",
            imageUrl = "https://example.com/pizza.jpg",
            rating = 4.8f,
            ratingCount = 543,
            address = "8 Tông Đản, Hoàn Kiếm, Hà Nội",
            priceRange = "100000",
            openingStatus = OpeningStatus.OPEN_24H,
            serviceType = ServiceType.FOOD,
            phoneNumber = "0987654321",
            likes = 324,
            categories = listOf("Pizza", "Ý", "Nhật")
        ),
        Restaurant(
            id = "3",
            name = "King BBQ",
            imageUrl = "https://example.com/bbq.jpg",
            rating = 4.3f,
            ratingCount = 876,
            address = "Vincom Bà Triệu, Hai Bà Trưng, Hà Nội",
            priceRange = "100000",
            openingStatus = OpeningStatus.SCHEDULED,
            businessHours = BusinessHours(
                openTime = "10:00",
                closeTime = "22:00"
            ),
            serviceType = ServiceType.FOOD,
            phoneNumber = "0345678912",
            likes = 234,
            categories = listOf("Lẩu", "Nướng", "Hàn Quốc")
        ),
        Restaurant(
            id = "4",
            name = "Highlands Coffee",
            imageUrl = "https://example.com/coffee.jpg",
            rating = 4.0f,
            ratingCount = 432,
            address = "54 Lý Thường Kiệt, Hoàn Kiếm, Hà Nội",
            priceRange = "100000",
            openingStatus = OpeningStatus.SCHEDULED,
            businessHours = BusinessHours(
                openTime = "07:00",
                closeTime = "23:00"
            ),
            serviceType = ServiceType.FOOD,
            phoneNumber = "0567891234",
            likes = 178,
            categories = listOf("Cà phê", "Trà", "Bánh ngọt")
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Tạm thời dùng ảnh local thay vì load từ URL
            Image(
                painter = painterResource(R.drawable.restaurant),
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
                        text = "${restaurant.rating} (${restaurant.ratingCount} ratings)  |  ",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${restaurant.distance} km",
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = restaurant.address,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Giá: ${restaurant.priceRange} VNĐ",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}