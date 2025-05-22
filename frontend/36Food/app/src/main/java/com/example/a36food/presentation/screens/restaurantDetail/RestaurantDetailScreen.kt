package com.example.a36food.presentation.screens.restaurantDetail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a36food.R
import com.example.a36food.domain.model.FoodItem
import com.example.a36food.domain.model.OpeningStatus
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.presentation.viewmodel.RestaurantDetailViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun RestaurantDetailScreen(
    viewModel: RestaurantDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onNetWorkError: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onFoodClick: (FoodItem) -> Unit = {} // Thêm tham số này
) {
    val state by viewModel.state.collectAsState()
    val cartState by viewModel.cartState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { viewModel.refreshData() }
    )

    LaunchedEffect(Unit) {
        viewModel.setNetworkErrorHandler{
            onNetWorkError()
        }
    }

    LaunchedEffect(cartState.success) {
        cartState.success?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearCartMessage()
        }
    }

    LaunchedEffect(cartState.error) {
        cartState.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "OK"
            )
            viewModel.clearCartMessage()
        }
    }

    Scaffold(
        topBar = {
            RestaurantDetailTopBar(
                title = state.restaurant?.name ?: "",
                onBackClick = onBackClick,
                onCartClick = onCartClick,
                onFavoriteClick = { viewModel.toggleFavorite() },
                scrollBehavior = scrollBehavior,
                isFavorite = state.restaurant?.isFavorite ?: false
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            if (state.isLoading && state.restaurant == null) {
                LoadingScreen()
            } else if (state.restaurant != null) {
                RestaurantDetailContent(
                    restaurant = state.restaurant!!,
                    menuItems = state.menuItems,
                    isMenuLoading = state.isMenuLoading,
                    menuCategories = state.menuCategories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) },
                    onAddToCart = { foodItem -> viewModel.showAddToCartDialog(foodItem) },
                    onFoodClick = onFoodClick, // Truyền tham số onFoodClick từ RestaurantDetailScreen
                    modifier = Modifier.padding(innerPadding)
                )
            }

            state.errorMessage?.let { message ->
                ErrorMessage(
                    message = message,
                    onDismiss = { viewModel.clearErrorMessage() }
                )
            }

            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            if (state.showAddToCartDiaLog && state.selectedFoodItem != null) {
                AddToCartDialog(
                    foodItem = state.selectedFoodItem!!,
                    quantity = state.itemQuantity,
                    note = state.itemNote,
                    onQuantityChange = { viewModel.updateItemQuantity(it) },
                    onNoteChange = { viewModel.updateItemNote(it) },
                    onDismiss = { viewModel.hideAddToCartDialog() },
                    onConfirm = { viewModel.addToCartWithDetails() },
                    isLoading = cartState.isLoading
                )
            }
        }
    }
}

@Composable
fun AddToCartDialog(
    foodItem: FoodItem,
    quantity: Int,
    note: String,
    onQuantityChange: (Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Thêm vào giỏ hàng",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Food name
                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                Text(
                    text = "${formatPrice(foodItem.price)}đ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Số lượng:",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { onQuantityChange(quantity - 1) },
                            enabled = quantity > 1
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Decrease quantity",
                                tint = if (quantity > 1)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Gray
                            )
                        }

                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        IconButton(
                            onClick = { onQuantityChange(quantity + 1) }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Increase quantity",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Note input
                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    label = { Text("Ghi chú (tùy chọn)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ví dụ: ít đá, không đường...") },
                    maxLines = 3
                )

                // Total price
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tổng cộng:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${formatPrice(foodItem.price * quantity)}đ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Thêm vào giỏ")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun RestaurantDetailContent(
    restaurant: Restaurant,
    menuItems: List<FoodItem>,
    isMenuLoading: Boolean,
    menuCategories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    onAddToCart: (FoodItem) -> Unit,
    onFoodClick: (FoodItem) -> Unit = {}, 
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            RestaurantHeaderSection(restaurant)
        }

        item {
            CategoryFilterSection(
                categories = menuCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )
        }

        when {
            isMenuLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            menuItems.isEmpty() -> {
                item {
                    EmptyMenuMessage()
                }
            }
            else -> {
                items(menuItems) { foodItem ->
                    MenuItemCard(
                        foodItem = foodItem,
                        onAddToCart = onAddToCart,
                        onFoodClick = onFoodClick
                    )
                }
            }
        }
    }
}

@Composable
fun RestaurantHeaderSection(restaurant: Restaurant) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Restaurant Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(restaurant.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Restaurant image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = painterResource(id = R.drawable.ic_broken_image),
                placeholder = painterResource(id = R.drawable.restaurant)
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = 500f
                        )
                    )
            )

            // Restaurant info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFA500),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${restaurant.rating}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = " (${restaurant.ratingCount} đánh giá)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Restaurant details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Address with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Address",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = restaurant.address,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Distance with icon - replaced ic_distance with DirectionsCar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = "Distance",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cách ${String.format("%.1f", restaurant.distance)} km",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (restaurant.durationInMinutes != null) {
                    Text(
                        text = " • ${restaurant.durationInMinutes} phút",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Opening hours with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "Opening hours",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                val openingText = when (restaurant.openingStatus) {
                    OpeningStatus.OPEN_24H -> "Mở cửa 24/7"
                    OpeningStatus.TEMPORARY_CLOSED -> "Tạm thời đóng cửa"
                    OpeningStatus.SCHEDULED -> restaurant.businessHours?.let {
                        "Mở cửa: ${it.openTime} - ${it.closeTime}"
                    } ?: "Giờ mở cửa không rõ"
                }

                Text(
                    text = openingText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Phone with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.Call,
                    contentDescription = "Phone",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = restaurant.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        HorizontalDivider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterSection(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Danh mục",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "Tất cả" filter chip
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Tất cả") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            // Category filter chips
            categories.forEach { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun MenuItemCard(
    foodItem: FoodItem,
    onAddToCart: (FoodItem) -> Unit,
    onFoodClick: (FoodItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onFoodClick(foodItem) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(foodItem.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Food image",
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_broken_image),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Food details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                if (foodItem.description.isNotBlank()) {
                    Text(
                        text = foodItem.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "${formatPrice(foodItem.price)}đ",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Add to cart button - now shows dialog instead of direct add
            IconButton(
                onClick = { onAddToCart(foodItem) },
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add to cart",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyMenuMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Restaurant,  // Using Restaurant icon instead of ic_empty_menu
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Không tìm thấy món ăn nào",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Lỗi",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.error,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Close,  // Using Close icon instead of ic_close
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailTopBar(
    title: String,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    isFavorite: Boolean = false
) {
    TopAppBar(
        title = { Text(title,style = MaterialTheme.typography.headlineMedium, color = Color.Black,) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            // Favorite heart icon
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else Color.Black
                )
            }

            IconButton(onClick = onCartClick) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    )
}

@SuppressLint("DefaultLocale")
fun formatPrice(price: Double): String {
    return "%,.0f".format(price)
}
