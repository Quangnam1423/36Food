package com.example.a36food.presentation.screens.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a36food.R
import com.example.a36food.domain.model.FoodItem
import com.example.a36food.domain.model.Review
import com.example.a36food.presentation.viewmodel.FoodViewModel
import com.example.a36food.presentation.viewmodel.RestaurantDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

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
                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${formatPrice(foodItem.price)}đ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    label = { Text("Ghi chú (tùy chọn)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ví dụ: ít đá, không đường...") },
                    maxLines = 3
                )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FoodScreen(
    foodItem: FoodItem,
    onBackClick: () -> Unit,
    onAddToCart: (FoodItem, Int, String) -> Unit,
    onNetworkError: () -> Unit = {},
    viewModel: FoodViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Refresh state for pull-to-refresh functionality
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isReviewsLoading,
        onRefresh = { viewModel.setFoodItem(foodItem) }
    )

    // Set the food item when the screen is first displayed
    LaunchedEffect(foodItem) {
        viewModel.setFoodItem(foodItem)
        viewModel.setNetworkErrorHandler {
            onNetworkError()
        }
    }

    // Show error message if there is one
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết món ăn",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF5722),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize()
            ) {
                // Food image
                item {
                    FoodImageSection(foodItem)
                }

                // Food details
                item {
                    FoodDetailsSection(foodItem, state.averageRating, state.reviews.size)
                }

                // Add to cart section
                item {
                    AddToCartSection(
                        quantity = state.quantity,
                        note = state.note,
                        onQuantityChanged = { viewModel.updateQuantity(it) },
                        onNoteChanged = { viewModel.updateNote(it) },
                        onAddToCart = {
                            viewModel.showAddToCartDialog()
                        },
                        price = foodItem.price
                    )
                }

                // Reviews header
                item {
                    ReviewsHeaderSection(state.reviews.size)
                }

                // Reviews list
                if (state.isReviewsLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (state.reviews.isEmpty()) {
                    item {
                        EmptyReviewsMessage()
                    }
                } else {
                    items(state.reviews) { review ->
                        ReviewItem(review)
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.isReviewsLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Hiển thị dialog thêm vào giỏ hàng nếu cần
            if (state.showAddToCartDiaLog && state.foodItem != null) {
                AddToCartDialog(
                    foodItem = state.foodItem!!,
                    quantity = state.quantity,
                    note = state.note,
                    onQuantityChange = { viewModel.updateQuantity(it) },
                    onNoteChange = { viewModel.updateNote(it) },
                    onDismiss = { viewModel.hideAddToCartDialog() },
                    onConfirm = { viewModel.addToCartWithDetails() },
                    isLoading = state.isLoading
                )
            }
        }
    }
}

@Composable
fun FoodImageSection(foodItem: FoodItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(foodItem.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Food image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            error = painterResource(id = R.drawable.ic_broken_image)
        )
    }
}

@Composable
fun FoodDetailsSection(
    foodItem: FoodItem,
    averageRating: Float,
    reviewCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = foodItem.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating and review count
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RatingBar(rating = averageRating)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$averageRating",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "($reviewCount đánh giá)",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Price
        Text(
            text = "${formatPrice(foodItem.price)}đ",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        if (foodItem.description.isNotBlank()) {
            Text(
                text = "Mô tả",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = foodItem.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()
    }
}

@Composable
fun AddToCartSection(
    quantity: Int,
    note: String,
    onQuantityChanged: (Int) -> Unit,
    onNoteChanged: (String) -> Unit,
    onAddToCart: () -> Unit,
    price: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Đặt hàng",
            style = MaterialTheme.typography.titleMedium,
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
                    onClick = { onQuantityChanged(quantity - 1) },
                    enabled = quantity > 1,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = if (quantity > 1) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease quantity",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.widthIn(min = 24.dp),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = { onQuantityChanged(quantity + 1) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Note input
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChanged,
            label = { Text("Ghi chú (tùy chọn)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ví dụ: ít đá, không đường...") },
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total price and add to cart button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tổng cộng:",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "${formatPrice(price * quantity)}đ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onAddToCart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Add to cart"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Thêm vào giỏ")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()
    }
}

@Composable
fun ReviewsHeaderSection(reviewCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Đánh giá ($reviewCount)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun EmptyReviewsMessage() {
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
                Icons.Default.RateReview,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Chưa có đánh giá nào",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(review.createdAt)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User info and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User avatar or icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (review.isAnonymous) "Người dùng ẩn danh" else "Người dùng #${review.userId}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                // Verified purchase badge if order ID is present
                if (review.orderId != null) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Đã mua",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rating
            RatingBar(rating = review.rating)

            Spacer(modifier = Modifier.height(12.dp))

            // Review content
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium
            )

            // Images if available
            if (review.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    review.imageUrls.forEach { imageUrl ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Review image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBar(rating: Float, maxRating: Int = 5) {
    Row {
        repeat(maxRating) { index ->
            val starIcon = when {
                index < rating.toInt() -> Icons.Default.Star
                index == rating.toInt() && rating % 1 > 0 -> Icons.Default.StarHalf
                else -> Icons.Default.StarOutline
            }

            Icon(
                imageVector = starIcon,
                contentDescription = null,
                tint = Color(0xFFFFA000),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Helper function to format price
fun formatPrice(price: Double): String {
    return String.format("%,.0f", price)
}

