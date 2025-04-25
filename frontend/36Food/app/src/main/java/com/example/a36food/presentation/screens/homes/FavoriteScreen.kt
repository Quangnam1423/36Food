package com.example.a36food.presentation.screens.homes

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.a36food.R
import com.example.a36food.domain.model.BusinessHours
import com.example.a36food.domain.model.OpeningStatus
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.domain.model.ServiceType
import com.example.a36food.ui.components.BottomNavBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoriteScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            FavoriteTopAppBar(
                onBackClick = { onNavigateToHome() }
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedRoute = Screen.Favorite.route,
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToSearch = onNavigateToSearch
            )
        }
    ) { paddingValues ->
        FavoriteLayout(
            modifier = Modifier.padding(paddingValues)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteTopAppBar(
    onBackClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Yêu thích",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = {onBackClick }) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFF5722)
        )
    )
}

@Composable
private fun FavoriteLayout(
    modifier: Modifier = Modifier
) {
    // Your favorite layout implementation here
    var selectedService by remember { mutableStateOf(ServiceType.ALL) }
    val restaurants = remember { generateSampleRestaurants() }
    val favoriteRestaurants = remember { generateSampleFavoriteRestaurants() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        // Service Type Filter
        item {
            ServiceTypeFilter(
                selectedService = selectedService,
                onServiceSelected = { selectedService = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Popular Restaurants Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Đặt Nhiều Nhất",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFFF5722),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(restaurants.take(19)) { restaurant ->
                        PopularRestaurantCard(restaurant)
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            OutlinedIconButton(
                                onClick = { /*TODO*/ },
                                border = BorderStroke(1.dp, Color(0xFFFF5722))
                            ) {
                                Icon(
                                    Icons.Default.ArrowForwardIos,
                                    contentDescription = "Xem thêm",
                                    tint = Color(0xFFFF5722)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Xem thêm",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFF5722)
                            )
                        }
                    }
                }
            }
}

@Composable
private fun ServiceTypeFilter(
    selectedService: ServiceType,
    onServiceSelected: (ServiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedService.toDisplayString(),
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            ServiceType.entries.forEach { serviceType ->
                DropdownMenuItem(
                    text = { Text(serviceType.toDisplayString()) },
                    onClick = {
                        onServiceSelected(serviceType)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RestaurantSection(restaurants: List<Restaurant>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Đặt Nhiều Nhất",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFFF5722),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(restaurants.take(19)) { restaurant ->
                PopularRestaurantCard(restaurant)
            }
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    OutlinedIconButton(
                        onClick = { /*TODO*/ },
                        border = BorderStroke(1.dp, Color(0xFFFF5722))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Xem thêm",
                            tint = Color(0xFFFF5722)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Xem thêm",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF5722)
                    )
                }
            }
        }
    }
}

@Composable
private fun PopularRestaurantCard(restaurant: Restaurant) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = restaurant.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FavoriteRestaurantCard(
    restaurant: Restaurant,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = restaurant.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = restaurant.priceRange,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF5722)
                    )
                }
            }

            // Heart icon
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(24.dp),
                tint = Color(0xFFFF5722)
            )
        }
    }
}

@Composable
private fun EmptyFavoriteState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Fastfood,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color(0xFFFF5722)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Yêu Quán từ món đầu tiên",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Món ngon hấp dẫn chiếm trọn trái tim!\nThả tim ngay để lưu quán bạn yêu nhé",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

private fun generateSampleRestaurants(): List<Restaurant> {
    return List(20) { index ->
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
        )
    }
}

private fun generateSampleFavoriteRestaurants(): List<Restaurant> {
    return List(5) { index ->
        Restaurant(
            id = "1",
            name = "Phở Thìn Bờ Hồ",
            imageUrl = "https://example.com/pho.jpg",
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
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteScreenPreview() {
    FavoriteScreen()
}