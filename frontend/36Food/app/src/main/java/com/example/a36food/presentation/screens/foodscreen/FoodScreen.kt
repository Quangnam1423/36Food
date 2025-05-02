package com.example.a36food.presentation.screens.foodscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.a36food.domain.model.FoodItem
import com.example.a36food.domain.model.FoodReview
import com.example.a36food.domain.model.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onAddToCartClick:() -> Unit = {},
    foodId: String,
) {
    val foodItem = SampleData.createSampleFood()
    val reviews = SampleData.createSampleReviews()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Food image section (3/7 of screen height)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((LocalConfiguration.current.screenHeightDp * 3/7).dp)
                ) {
                    // Food image
                    AsyncImage(
                        model = foodItem.imageUrl,
                        contentDescription = "Food image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Transparent gradient overlay for better text visibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Top app bar
                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = onShareClick) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            }

            // Food details section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Tên món ăn",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Mô tả chi tiết về món ăn này, bao gồm các thành phần và cách chế biến...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatItem(Icons.Default.ShoppingCart, "1.2k", "Đã bán")
                        StatItem(Icons.Default.Favorite, "4.5k", "Lượt thích")
                    }
                }
            }

            // Price and Add to Cart section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "69.000đ",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFF5722),
                        fontWeight = FontWeight.Bold
                    )

                    // add to cart button
                    IconButton(
                        onClick = { onAddToCartClick() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(0xFFFF5722),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to cart",
                            tint = Color.White
                        )
                    }
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray
                )
            }

            // Reviews section
            item {
                Text(
                    text = "Đánh giá từ khách hàng",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Review items
            items(5) { index ->
                ReviewItem(
                    isAnonymous = index % 2 == 0,
                    rating = 4.5f,
                    comment = "Món ăn rất ngon, phục vụ nhanh. Sẽ quay lại lần sau!",
                    date = "20/03/2024 15:30"
                )
                if (index < 4) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun ReviewItem(
    isAnonymous: Boolean,
    rating: Float,
    comment: String,
    date: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar or anonymous icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isAnonymous) Color.LightGray else Color.Transparent)
            ) {
                if (!isAnonymous) {
                    AsyncImage(
                        model = "https://example.com/avatar.jpg",
                        contentDescription = "User avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (isAnonymous) "Ẩn danh" else "Nguyễn Văn A",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < rating) Color(0xFFFFB300) else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = comment,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

object SampleData {
    fun createSampleFood(): FoodItem {
        return FoodItem(
            id = "food_1",
            restaurantId = "rest_1",
            name = "Phở bò đặc biệt",
            description = "Phở bò với nước dùng ngọt thanh từ xương hầm 24 giờ, kèm thêm bắp bò, gầu, nạm, " +
                    "tái và các loại rau thơm tươi ngon. Được nấu theo công thức gia truyền hơn 50 năm.",
            price = 69000.0,
            imageUrl = "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43",
            category = "Phở",
            isAvailable = true,
            isPopular = true,
            likes = 4567,
            saleCount = 1234,
            createdAt = System.currentTimeMillis()
        )
    }

    fun createSampleReviews(): List<FoodReview> {
        return listOf(
            FoodReview(
                review = Review(
                    id = "review_1",
                    userId = "user_1",
                    content = "Phở ngon tuyệt vời, nước dùng đậm đà, thịt bò tươi ngon. Quán phục vụ nhanh và nhiệt tình. Chắc chắn sẽ quay lại!",
                    rating = 5f,
                    imageUrls = listOf(
                        "https://images.unsplash.com/photo-1576644461179-ac01048e621f",
                    ),
                    likes = 12,
                    createdAt = System.currentTimeMillis() - 86400000, // 1 day ago
                    isAnonymous = false
                ),
                foodId = "food_1",
                restaurantId = "rest_1",
                orderId = "order_1"
            ),
            FoodReview(
                review = Review(
                    id = "review_2",
                    userId = "user_2",
                    content = "Phần ăn nhiều, thịt bò tươi. Tuy nhiên giá hơi cao so với mặt bằng chung.",
                    rating = 4f,
                    likes = 5,
                    createdAt = System.currentTimeMillis() - 172800000, // 2 days ago
                    isAnonymous = true
                ),
                foodId = "food_1",
                restaurantId = "rest_1",
                orderId = "order_2"
            ),
            FoodReview(
                review = Review(
                    id = "review_3",
                    userId = "user_3",
                    content = "Quán sạch sẽ, nhân viên phục vụ nhiệt tình. Sẽ giới thiệu cho bạn bè.",
                    rating = 5f,
                    imageUrls = listOf(
                        "https://images.unsplash.com/photo-1583057341910-49cc0d7c8989",
                        "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43"
                    ),
                    likes = 8,
                    createdAt = System.currentTimeMillis() - 259200000, // 3 days ago
                    isAnonymous = false
                ),
                foodId = "food_1",
                restaurantId = "rest_1",
                orderId = "order_3"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FoodDetailScreenPreview() {
    val food = SampleData.createSampleFood()

    FoodDetailScreen(
        foodId = "1",
        onBackClick = {},
        onShareClick = {},
        onAddToCartClick = {}
    )
}