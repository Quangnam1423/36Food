package com.example.a36food.presentation.screens.orderdetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.a36food.presentation.viewmodel.HistoryViewModel
import java.text.NumberFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderDetailScreen(
    orderId: Long,
    viewModel: HistoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val orderDetails = state.orderDetails
    val isLoading = state.isLoadingDetails
    val error = state.error

    LaunchedEffect(key1 = orderId) {
        viewModel.getOrderDetails(orderId)
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            viewModel.clearOrderDetails()
        }
    }

    Scaffold(
        topBar = {
            OrderDetailTopBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.getOrderDetails(orderId) }
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
                orderDetails != null -> {
                    OrderDetailsContent(orderDetails)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Chi tiết đơn hàng",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFF5722),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}

@Composable
fun OrderDetailsContent(orderDetails: Map<String, Any>) {
    val scrollState = rememberScrollState()

    // Format currency
    val formatter = NumberFormat.getCurrencyInstance()
    formatter.currency = Currency.getInstance("VND")
    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Order ID and Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val orderId = orderDetails["orderId"] as? Number
                val status = orderDetails["status"] as? String ?: "UNKNOWN"

                Text(
                    text = "Mã đơn hàng: #${orderId?.toLong() ?: "N/A"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                val statusColor = when (status) {
                    "PENDING" -> Color(0xFFFF9800) // Orange
                    "CONFIRMED", "PREPARING", "READY" -> Color(0xFF2196F3) // Blue
                    "DELIVERING" -> Color(0xFF9C27B0) // Purple
                    "COMPLETED" -> Color(0xFF4CAF50) // Green
                    "CANCELED" -> Color(0xFFF44336) // Red
                    else -> Color.Gray
                }

                val statusText = when (status) {
                    "PENDING" -> "Đang chờ xác nhận"
                    "CONFIRMED" -> "Đã xác nhận"
                    "PREPARING" -> "Đang chuẩn bị"
                    "READY" -> "Sẵn sàng giao"
                    "DELIVERING" -> "Đang giao"
                    "COMPLETED" -> "Hoàn thành"
                    "CANCELED" -> "Đã huỷ"
                    "DRAFT" -> "Nháp"
                    else -> status
                }

                Text(
                    text = "Trạng thái: $statusText",
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                val createdAt = orderDetails["orderDate"] as? String
                if (createdAt != null) {
                    Text(
                        text = "Ngày đặt: $createdAt",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Restaurant info
        val restaurantId = orderDetails["restaurantId"] as? Number
        val restaurantAddress = orderDetails["restaurantAddress"] as? String

        if (restaurantId != null && restaurantAddress != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Nhà hàng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Nhà hàng #${restaurantId.toLong()}", fontWeight = FontWeight.SemiBold)
                    Text(text = restaurantAddress)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Order items
        @Suppress("UNCHECKED_CAST")
        val orderItems = orderDetails["items"] as? List<Map<String, Any>> ?: emptyList()

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Món đã đặt",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                orderItems.forEach { item ->
                    val itemId = item["id"] as? Number
                    val name = item["name"] as? String ?: "Unknown"
                    val quantity = item["quantity"] as? Number ?: 0
                    val price = item["price"] as? Number ?: 0
                    val imageUrl = item["imageUrl"] as? String
                    val note = item["note"] as? String

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!imageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = name,
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = formatter.format(price.toDouble()),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            if (!note.isNullOrBlank()) {
                                Text(
                                    text = "Ghi chú: $note",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFF5722),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            text = "x$quantity",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        val totalPrice = quantity.toInt() * price.toDouble()
                        Text(
                            text = formatter.format(totalPrice),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (orderItems.indexOf(item) < orderItems.size - 1) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Order summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tổng cộng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                val subtotal = orderDetails["itemsTotal"] as? Number ?: 0
                val deliveryFee = orderDetails["deliveryFee"] as? Number ?: 0
                val total = orderDetails["totalAmount"] as? Number ?: 0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tạm tính", style = MaterialTheme.typography.bodyMedium)
                    Text(text = formatter.format(subtotal), style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Phí vận chuyển", style = MaterialTheme.typography.bodyMedium)
                    Text(text = formatter.format(deliveryFee), style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tổng cộng",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = formatter.format(total),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5722),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delivery address
        val customerAddress = orderDetails["customerAddress"] as? String
        val shippingAddress = orderDetails["shippingAddress"] as? String

        val addressToShow = shippingAddress ?: customerAddress

        if (!addressToShow.isNullOrEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Địa chỉ giao hàng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = addressToShow)
                }
            }
        }

        // Notes
        val note = orderDetails["note"] as? String
        if (!note.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Ghi chú",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = note)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
