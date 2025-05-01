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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a36food.R
import com.example.a36food.Screen
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "location",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = location,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 16.sp
                    )
                )
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "fix location",
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        actions = {
            CartIcon(
                cartCount = 3,
                onClick = {},
            )
            IconButton(
                onClick = {},
                modifier = Modifier.size(40.dp)
            ) {
                MessageIcon(
                    messageCount = 3,
                    onClick = {}
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFF5722),
            titleContentColor = Color.White
        ),
        modifier = Modifier.height(56.dp)
    )
}


@Composable
private fun MenuLayout(
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(FilterOption.NEAR_ME) }
    val restaurantList = createRestaurantList()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Welcome Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    text = "Chúc Bạn Ngon Miệng, Bình!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        lineHeight = 24.sp
                    ),
                    color = Color(0xFFFF9800),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        // Categories Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFAFAFA)
                ),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Danh Mục",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFFFF5722)
                        )

                        Text(
                            "Tất cả >",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp
                            ),
                            color = Color.Gray,
                            modifier = Modifier.clickable { /* TODO */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryGrid(onCategoryClick = {})
                }
            }
        }

        // Filter Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFAFAFA)
                ),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                FilterBar(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Restaurant List
        val filteredRestaurants = when (selectedFilter) {
            FilterOption.NEAR_ME -> restaurantList.sortedBy { it.distance }
            FilterOption.POPULAR -> restaurantList.sortedByDescending { it.ratingCount }
            FilterOption.TOP_RATED -> restaurantList.sortedByDescending { it.rating }
        }

        items(filteredRestaurants) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CategoryGrid(
    onCategoryClick: (String) -> Unit
) {
    val categories = listOf(
        "Cơm" to R.drawable.rice,
        "Phở" to R.drawable.pho,
        "Pizza" to R.drawable.pizza,
        "Burger" to R.drawable.burger,
        "Đồ ăn nhanh" to R.drawable.fast_food,
        "Đồ uống" to R.drawable.drink
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.height(180.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (name, imageRes) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategoryClick(name) }
                    .padding(4.dp)
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RestaurantCard(
    restaurant: Restaurant,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current).data(restaurant.imageUrl)
                    .crossfade(true)
                    .build(),
                error = painterResource(R.drawable.ic_broken_image),
                placeholder = painterResource(R.drawable.restaurant),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFA500),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${restaurant.rating}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = " (${restaurant.ratingCount})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = Color.Gray
                    )
                }

                Text(
                    text = restaurant.address,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Giá: ${restaurant.priceRange}đ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFFFF5722)
                )
            }
        }
    }
}

enum class FilterOption(val title: String, val icon: ImageVector) {
    NEAR_ME("Gần tôi", Icons.Default.LocationOn),
    POPULAR("Bán chạy", Icons.Default.LocalFireDepartment),
    TOP_RATED("Đánh giá", Icons.Default.Star)
}

@Composable
private fun FilterBar(
    selectedFilter: FilterOption,
    onFilterSelected: (FilterOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FilterOption.values().forEachIndexed { index, filter ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onFilterSelected(filter) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = filter.icon,
                        contentDescription = null,
                        tint = if (selectedFilter == filter) Color(0xFFFF5722) else Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = filter.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 11.sp,
                            fontWeight = if (selectedFilter == filter) FontWeight.Medium else FontWeight.Normal,
                        ),
                        color = if (selectedFilter == filter) Color(0xFFFF5722) else Color.Gray,
                    )
                }
                if (index < FilterOption.values().size - 1) {
                    Text(
                        text = "|",
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    filter: FilterOption,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = filter.icon,
            contentDescription = null,
            tint = if (selected) Color(0xFFFF5722) else Color.Gray,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = filter.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            ),
            color = if (selected) Color(0xFFFF5722) else Color.Gray
        )
    }
}

private fun createRestaurantList() : List<Restaurant>{
    return listOf(
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
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}