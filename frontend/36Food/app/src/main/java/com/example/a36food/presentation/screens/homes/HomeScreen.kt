package com.example.a36food.presentation.screens.homes

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a36food.R
import com.example.a36food.Screen
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.presentation.viewmodel.HomeViewModel
import com.example.a36food.ui.components.BottomNavBar
import com.example.a36food.ui.components.CartIcon
import com.example.a36food.ui.components.MessageIcon
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

enum class FilterOption(val title: String, val icon: ImageVector) {
    NEAR_ME("Gần tôi", Icons.Default.LocationOn),
    POPULAR("Bán chạy", Icons.Default.LocalFireDepartment),
    TOP_RATED("Đánh giá", Icons.Default.Star)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSearch: () -> Unit = {},
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onRestaurantClick: (String) -> Unit = {},
    onNetworkError: () -> Unit = {}
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isRefreshing)


    LaunchedEffect(Unit) {
        viewModel.setNetworkErrorHandler {
            onNetworkError()
        }
    }

    Scaffold (
        topBar = {
            HomeTopAppBar(
                location = state.userAddress,
                isLoading = state.isLocationLoading,
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
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {viewModel.refreshData()}
        ) {
            RestaurantListLayout(
                restaurants = state.restaurants,
                isInitialLoading = state.isInitialLoading,
                isLoadingMore = state.isLoadingMore,
                hasMore = state.hasMore,
                selectedFilter = state.selectedFilter,
                onFilterSelected = { viewModel.setSelectedFilter(it) },
                onLoadMore = { viewModel.loadMoreRestaurants() },
                onRestaurantClick = onRestaurantClick,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    location: String,
    isLoading: Boolean = false,
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

                // Display the address with a conditional for loading state
                if (isLoading) {
                    Text(
                        text = "Đang lấy vị trí...",
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            lineHeight = 16.sp
                        )
                    )
                } else {
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
                }

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
private fun RestaurantListLayout(
    restaurants: List<Restaurant>,
    isInitialLoading: Boolean,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    selectedFilter: FilterOption,
    onFilterSelected: (FilterOption) -> Unit,
    onLoadMore: () -> Unit,
    onRestaurantClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Calculate when to load more items
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= restaurants.size - 2 && hasMore && !isInitialLoading && !isLoadingMore
        }
    }

    // Effect to trigger load more when close to the bottom
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
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
                    containerColor = Color(0xFFFFF8E1)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    text = "Chào mừng bạn đến với 36Food! Bạn đang đói bụng ư? Hãy để chúng tôi giúp bạn.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
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
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Danh mục",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { /* TODO: Navigate to categories */ }
                        ) {
                            Text(
                                text = "Xem tất cả",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFF5722)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "View all",
                                tint = Color(0xFFFF5722),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                FilterBar(
                    selectedFilter = selectedFilter,
                    onFilterSelected = onFilterSelected,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Initial loading indicator (when no restaurants are loaded yet)
        if (isInitialLoading && restaurants.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFF5722))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Đang tải danh sách nhà hàng...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Restaurant List
        items(restaurants) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                modifier = Modifier.fillMaxWidth(),
                onRestaurantClick = onRestaurantClick
            )
        }

        // Loading indicator at the bottom when loading more
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFFFF5722),
                        strokeWidth = 2.dp
                    )
                }
            }
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
    modifier: Modifier = Modifier,
    onRestaurantClick: (String) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {onRestaurantClick(restaurant.id) },
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
                        text = " (${restaurant.ratingCount} lượt đánh giá)",
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
        FilterOption.entries.forEachIndexed { index, filter ->
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
                if (index < FilterOption.entries.size - 1) {
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
