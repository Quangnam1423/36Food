package com.example.a36food.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a36food.domain.model.OrderStatus

@Composable
fun OrderDetailScreen() {
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                OrderDetailAppBar(
                    onBackClicked = {},
                    onMoreClicked = { showBottomSheet = true }
                )
            }
        ) { paddingValues ->
            OrderDetailLayout(modifier = Modifier.padding(paddingValues))
        }

        if (showBottomSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showBottomSheet = false }
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF5722)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFFF5722))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Trợ giúp",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        ),
                        border = BorderStroke(1.dp, Color.Red)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Xóa đơn hàng",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )

                    Divider(
                        color = Color(0xFFE0E0E0),
                        thickness = 1.dp
                    )

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )

                    OutlinedButton(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF5722)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFFF5722))
                    ) {
                        Text(
                            text = "Hủy",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailAppBar(
    onBackClicked: () -> Unit,
    onMoreClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text (
                text = "Chi Tiết Đơn Hàng",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onMoreClicked) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More options",
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
fun OrderDetailLayout(
    modifier: Modifier = Modifier,
    isOrderCompleted: Boolean = true,
    orderStatus: OrderStatus = OrderStatus.COMPLETED
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Status Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when(orderStatus) {
                        OrderStatus.PENDING -> "Chờ xác nhận"
                        OrderStatus.CONFIRMED -> "Đã xác nhận"
                        OrderStatus.PREPARING -> "Đang chuẩn bị"
                        OrderStatus.READY_TO_PICKUP -> "Chờ lấy hàng"
                        OrderStatus.PICKED_UP -> "Đã lấy hàng"
                        OrderStatus.IN_PROGRESS -> "Đang giao"
                        OrderStatus.COMPLETED -> "Hoàn thành"
                        OrderStatus.CANCELLED -> "Đã hủy"
                        OrderStatus.REFUNDED -> "Đã hoàn tiền"
                        OrderStatus.FAILED -> "Giao hàng thất bại"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = when(orderStatus) {
                        OrderStatus.COMPLETED -> Color(0xFF4CAF50)
                        OrderStatus.CANCELLED, OrderStatus.FAILED -> Color.Red
                        OrderStatus.IN_PROGRESS,
                        OrderStatus.PICKED_UP,
                        OrderStatus.READY_TO_PICKUP -> Color(0xFF2196F3)
                        OrderStatus.PREPARING,
                        OrderStatus.CONFIRMED -> Color(0xFFFF9800)
                        OrderStatus.PENDING -> Color(0xFF9E9E9E)
                        OrderStatus.REFUNDED -> Color(0xFF673AB7)
                    }
                )

                Text(
                    text = "Nếu cần hỗ trợ thêm, bạn vui lòng truy cập Trung tâm Trợ giúp nhé.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Icon(
                imageVector = when(orderStatus) {
                    OrderStatus.COMPLETED -> Icons.Rounded.CheckCircle
                    OrderStatus.IN_PROGRESS,
                    OrderStatus.PICKED_UP -> Icons.Default.LocalShipping
                    OrderStatus.CANCELLED,
                    OrderStatus.FAILED -> Icons.Rounded.Cancel
                    else -> Icons.Default.LocalShipping
                },
                contentDescription = null,
                tint = when(orderStatus) {
                    OrderStatus.COMPLETED -> Color(0xFF4CAF50)
                    OrderStatus.CANCELLED, OrderStatus.FAILED -> Color.Red
                    OrderStatus.IN_PROGRESS,
                    OrderStatus.PICKED_UP -> Color(0xFF2196F3)
                    else -> Color.Gray
                },
                modifier = Modifier.size(32.dp)
            )
        }

        OrderProgressBar(orderStatus)
        StatusMessageSection(orderStatus)
        // Address Section
        AddressSection()

        // Order Details
        Column {
            Text(
                text = "Chi tiết đơn hàng",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            OrderItems()
            OrderSummary()
        }

        // Additional Info
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow("Ghi chú:", "Không cần ớt")
            InfoRow("Dụng cụ ăn uống:", "Có")
            InfoRow("Mã đơn hàng:", "ORDER123")
            InfoRow("Thời gian đặt:", "15:30 12/03/2024")
            InfoRow("Thanh toán:", "Tiền mặt")
        }

        // Reorder Button
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF5722)
            )
        ) {
            Text(
                text = "Đặt lại",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun OrderProgressBar(orderStatus: OrderStatus) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Đặt đơn
        OrderProgressStep(
            icon = Icons.Default.ShoppingCart,
            label = "Đặt đơn",
            isActive = orderStatus == OrderStatus.PENDING,
            isCompleted = orderStatus != OrderStatus.PENDING
        )
        OrderProgressLine(
            isCompleted = orderStatus != OrderStatus.PENDING && orderStatus != OrderStatus.CANCELLED
        )

        // Xác nhận
        OrderProgressStep(
            icon = Icons.Default.CheckCircle,
            label = "Xác nhận",
            isActive = orderStatus == OrderStatus.CONFIRMED || orderStatus == OrderStatus.PREPARING,
            isCompleted = orderStatus in listOf(
                OrderStatus.READY_TO_PICKUP,
                OrderStatus.PICKED_UP,
                OrderStatus.IN_PROGRESS,
                OrderStatus.COMPLETED
            )
        )
        OrderProgressLine(
            isCompleted = orderStatus in listOf(
                OrderStatus.READY_TO_PICKUP,
                OrderStatus.PICKED_UP,
                OrderStatus.IN_PROGRESS,
                OrderStatus.COMPLETED
            )
        )

        // Đang giao
        OrderProgressStep(
            icon = Icons.Default.LocalShipping,
            label = "Đang giao",
            isActive = orderStatus in listOf(
                OrderStatus.READY_TO_PICKUP,
                OrderStatus.PICKED_UP,
                OrderStatus.IN_PROGRESS
            ),
            isCompleted = orderStatus == OrderStatus.COMPLETED
        )
        OrderProgressLine(
            isCompleted = orderStatus == OrderStatus.COMPLETED
        )

        // Hoàn thành
        OrderProgressStep(
            icon = Icons.Default.CheckCircle,
            label = "H.thành",
            isActive = false,
            isCompleted = orderStatus == OrderStatus.COMPLETED
        )
    }
}

@Composable
private fun StatusMessageSection(orderStatus: OrderStatus) {
    when (orderStatus) {
        OrderStatus.COMPLETED, OrderStatus.CANCELLED, OrderStatus.FAILED -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when(orderStatus) {
                        OrderStatus.COMPLETED -> "Đơn hoàn tất"
                        OrderStatus.CANCELLED -> "Lý do hủy:"
                        OrderStatus.FAILED -> "Lý do thất bại:"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (orderStatus == OrderStatus.COMPLETED) {
                    IconButton(onClick = { /* TODO: Navigate to chat */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Message,
                            contentDescription = "Chat",
                            tint = Color(0xFFFF5722)
                        )
                    }
                }
            }
        }
        else -> { /* Không hiển thị gì */ }
    }
}


@Composable
private fun OrderProgressLine(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .width(32.dp)
            .height(2.dp)
            .background(
                color = if (isCompleted) Color(0xFFFF5722) else Color.Gray.copy(alpha = 0.2f)
            )
    )
}

@Composable
private fun OrderProgressStep(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(64.dp) // Giảm width từ 72dp xuống 64dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp) // Giảm size từ 40dp xuống 32dp
                .background(
                    color = when {
                        isCompleted -> Color(0xFFFF5722)
                        isActive -> Color(0xFFFF5722).copy(alpha = 0.2f)
                        else -> Color.Gray.copy(alpha = 0.2f)
                    },
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when {
                    isCompleted -> Color.White
                    isActive -> Color(0xFFFF5722)
                    else -> Color.Gray
                },
                modifier = Modifier.size(20.dp) // Giảm size từ 24dp xuống 20dp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isActive || isCompleted) Color(0xFFFF5722) else Color.Gray,
            modifier = Modifier.padding(horizontal = 2.dp), // Giảm padding từ 4dp xuống 2dp
            maxLines = 1 // Giới hạn text trên 1 dòng
        )
    }
}


@Composable
private fun AddressSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "* Từ:",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "123 Đường ABC, Quận XYZ",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column {
            Text(
                text = "* Đến:",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "456 Đường DEF, Quận UVW\nNgười nhận: Nguyễn Văn A\nSĐT: 0123456789",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun OrderItemsSection() {
    Column {
        Text(
            text = "Chi tiết đơn hàng",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Order items list
        OrderItems()

        // Order summary
        OrderSummary()
    }
}

@Composable
private fun AdditionalInfoSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoRow("Ghi chú:", "Không cần ớt")
        InfoRow("Dụng cụ ăn uống:", "Có")
        InfoRow("Mã đơn hàng:", "ORDER123")
        InfoRow("Thời gian đặt:", "15:30 12/03/2024")
        InfoRow("Thanh toán:", "Tiền mặt")
    }
}


@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun OrderItems() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Example item
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = "2x",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Phở bò tái",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "120.000đ",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun OrderSummary() {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Divider(color = Color.LightGray)

        InfoRow("Tổng (3 món):", "360.000đ")
        InfoRow("Phí giao hàng (2.5km):", "15.000đ")
        InfoRow("Giảm giá:", "-20.000đ")

        Divider(color = Color.LightGray)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tổng cộng:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "355.000đ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722)
            )
        }
    }
}

@Preview(showBackground = true, name = "Default")
@Composable
fun OrderDetailScreenDefaultPreview() {
    OrderDetailScreen()
}

@Preview(showBackground = true, name = "Order Completed")
@Composable
fun OrderDetailScreenCompletedPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                OrderDetailAppBar(
                    onBackClicked = {},
                    onMoreClicked = {}
                )
            }
        ) { paddingValues ->
            OrderDetailLayout(
                modifier = Modifier.padding(paddingValues),
                isOrderCompleted = true,
                orderStatus = OrderStatus.COMPLETED
            )
        }
    }
}

@Preview(showBackground = true, name = "Order In Progress")
@Composable
fun OrderDetailScreenInProgressPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                OrderDetailAppBar(
                    onBackClicked = {},
                    onMoreClicked = {}
                )
            }
        ) { paddingValues ->
            OrderDetailLayout(
                modifier = Modifier.padding(paddingValues),
                isOrderCompleted = false,
                orderStatus = OrderStatus.IN_PROGRESS
            )
        }
    }
}

@Preview(showBackground = true, name = "Order Cancelled")
@Composable
fun OrderDetailScreenCancelledPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                OrderDetailAppBar(
                    onBackClicked = {},
                    onMoreClicked = {}
                )
            }
        ) { paddingValues ->
            OrderDetailLayout(
                modifier = Modifier.padding(paddingValues),
                isOrderCompleted = false,
                orderStatus = OrderStatus.CANCELLED
            )
        }
    }
}