package com.example.a36food.presentation.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.a36food.R
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.domain.model.ServiceType
import com.example.a36food.ui.components.BottomNavBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoriteScreen() {
    Scaffold(
        topBar = { FavoriteTopAppBar() },
        bottomBar = { BottomNavBar() }
    ) { paddingValues ->
        FavoriteContent(modifier = Modifier.padding(paddingValues))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteTopAppBar() {
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
            IconButton(onClick = { /*TODO*/ }) {
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
private fun FavoriteContent(modifier: Modifier = Modifier) {
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

        // Favorite Restaurants Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Quán Yêu Thích",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFFF5722),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (favoriteRestaurants.isEmpty()) {
            item {
                EmptyFavoriteState()
            }
        } else {
            items(favoriteRestaurants) { restaurant ->
                FavoriteRestaurantCard(
                    restaurant = restaurant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                model = restaurant.imageRes,
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
                    model = restaurant.imageRes,
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
            id = index,
            name = "Nhà hàng ${index + 1}",
            imageRes = R.drawable.restaurant,  // Thêm placeholder image vào res/drawable
            rating = 4.5f,
            address = "Địa chỉ ${index + 1}",
            priceRange = "50.000đ - 200.000đ",
            isOpen = true
        )
    }
}

private fun generateSampleFavoriteRestaurants(): List<Restaurant> {
    return List(5) { index ->
        Restaurant(
            id = 100 + index,  // ID khác với nhà hàng thông thường
            name = "Nhà hàng yêu thích ${index + 1}",
            imageRes = R.drawable.restaurant,
            rating = 4.8f,
            address = "Địa chỉ yêu thích ${index + 1}",
            priceRange = "100.000đ - 300.000đ",
            isOpen = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteScreenPreview() {
    FavoriteScreen()
}