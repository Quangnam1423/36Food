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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a36food.ui.components.BottomNavBar
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import com.example.a36food.domain.OrderStatus
import com.example.a36food.domain.model.Order
import com.example.a36food.domain.model.OrderItem
import com.example.a36food.domain.model.ServiceType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderHistoryScreen(

) {

    Scaffold(
        topBar = {
            OrderHistoryTopAppBar()
        },
        bottomBar = {
            BottomNavBar()
        }
    ) { paddingValues ->
        OrderHistoryLayout(modifier = Modifier.padding(paddingValues))

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryTopAppBar(

) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Đơn Hàng",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        },
        actions = {

        },
        navigationIcon = {
            IconButton(
                onClick = {/*TO DO*/}
            ) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFF5722), // Màu đỏ cam ShopeeFood
            titleContentColor = Color.White
        )
    )
}

@Composable
fun OrderHistoryLayout(
    modifier: Modifier = Modifier,

    ) {

    val testOrders = listOf(
        // Đơn đang đến
        Order(
            orderId = "HD001",
            restaurantName = "Phở 24",
            restaurantImage = "https://example.com/pho24.jpg",
            orderItems = listOf(
                OrderItem(id = "1", name = "Phở bò tái", quantity = 2, price = 55000.0, imageUrl = "https://example.com/pho.jpg"),
                OrderItem(id = "2", name = "Nước ngọt", quantity = 2, price = 15000.0, imageUrl = "https://example.com/drink.jpg")
            ),
            totalPrice = 140000.0,
            orderDate = System.currentTimeMillis(),
            status = OrderStatus.UPCOMING,
            deliveryAddress = "123 Nguyễn Văn Cừ, Q.5, TP.HCM",
            paymentMethod = "Tiền mặt",
            serviceType = ServiceType.FOOD,
            isCompleted = false
        ),
        Order(
            orderId = "HD002",
            restaurantName = "Bếp Thái",
            restaurantImage = "https://example.com/bepthai.jpg",
            orderItems = listOf(
                OrderItem(id = "3", name = "Pad Thai", quantity = 1, price = 75000.0, imageUrl = "https://example.com/padthai.jpg"),
            ),
            totalPrice = 75000.0,
            orderDate = System.currentTimeMillis(),
            status = OrderStatus.UPCOMING,
            deliveryAddress = "45 Lý Tự Trọng, Q.1, TP.HCM",
            paymentMethod = "Ví Momo",
            serviceType = ServiceType.FOOD,
            isCompleted = false
        ),

        // Đơn đang xử lý
        Order(
            orderId = "HD003",
            restaurantName = "Circle K",
            restaurantImage = "https://example.com/circlek.jpg",
            orderItems = listOf(
                OrderItem(id = "4", name = "Snack Mix", quantity = 2, price = 25000.0, imageUrl = "https://example.com/snack.jpg")
            ),
            totalPrice = 50000.0,
            orderDate = System.currentTimeMillis() - 86400000,
            status = OrderStatus.IN_PROGRESS,
            deliveryAddress = "67 Lê Lợi, Q.1, TP.HCM",
            paymentMethod = "ZaloPay",
            serviceType = ServiceType.SUPERMARKET,
            isCompleted = false
        ),

        // Đơn đã hoàn thành
        Order(
            orderId = "HD004",
            restaurantName = "Nhà thuốc An Khang",
            restaurantImage = "https://example.com/ankhang.jpg",
            orderItems = listOf(
                OrderItem(id = "5", name = "Vitamin C", quantity = 1, price = 85000.0, imageUrl = "https://example.com/vitamin.jpg")
            ),
            totalPrice = 85000.0,
            orderDate = System.currentTimeMillis() - 172800000,
            status = OrderStatus.DELIVERED,
            deliveryAddress = "78 Võ Văn Tần, Q.3, TP.HCM",
            paymentMethod = "ZaloPay",
            serviceType = ServiceType.MEDICINE,
            isCompleted = true
        ),
        Order(
            orderId = "HD005",
            restaurantName = "BigC",
            restaurantImage = "https://example.com/bigc.jpg",
            orderItems = listOf(
                OrderItem(id = "6", name = "Gạo ST25", quantity = 2, price = 180000.0, imageUrl = "https://example.com/rice.jpg"),
                OrderItem(id = "7", name = "Nước mắm", quantity = 1, price = 45000.0, imageUrl = "https://example.com/fishsauce.jpg")
            ),
            totalPrice = 405000.0,
            orderDate = System.currentTimeMillis() - 259200000,
            status = OrderStatus.DELIVERED,
            deliveryAddress = "34 CMT8, Q.3, TP.HCM",
            paymentMethod = "Thẻ tín dụng",
            serviceType = ServiceType.GROCERY,
            isCompleted = true
        ),

        // Đơn đã hủy
        Order(
            orderId = "HD006",
            restaurantName = "Flower Box",
            restaurantImage = "https://example.com/flowerbox.jpg",
            orderItems = listOf(
                OrderItem(id = "8", name = "Bó hoa hồng", quantity = 1, price = 350000.0, imageUrl = "https://example.com/rose.jpg")
            ),
            totalPrice = 350000.0,
            orderDate = System.currentTimeMillis() - 345600000,
            status = OrderStatus.CANCELLED,
            deliveryAddress = "156 Trần Hưng Đạo, Q.5, TP.HCM",
            paymentMethod = "Tiền mặt",
            serviceType = ServiceType.FLOWER,
            isCompleted = false
        ),
        Order(
            orderId = "HD007",
            restaurantName = "Heniken Store",
            restaurantImage = "https://example.com/heniken.jpg",
            orderItems = listOf(
                OrderItem(id = "9", name = "Bia Heniken", quantity = 24, price = 450000.0, imageUrl = "https://example.com/beer.jpg")
            ),
            totalPrice = 450000.0,
            orderDate = System.currentTimeMillis() - 432000000,
            status = OrderStatus.CANCELLED,
            deliveryAddress = "89 Nguyễn Du, Q.1, TP.HCM",
            paymentMethod = "Tiền mặt",
            serviceType = ServiceType.ALCOHOL,
            isCompleted = false
        ),

        // Thêm đơn hoàn thành khác
        Order(
            orderId = "HD008",
            restaurantName = "PetMart",
            restaurantImage = "https://example.com/petmart.jpg",
            orderItems = listOf(
                OrderItem(id = "10", name = "Thức ăn cho mèo", quantity = 2, price = 120000.0, imageUrl = "https://example.com/catfood.jpg")
            ),
            totalPrice = 240000.0,
            orderDate = System.currentTimeMillis() - 518400000,
            status = OrderStatus.DELIVERED,
            deliveryAddress = "34 Nguyễn Thị Minh Khai, Q.1, TP.HCM",
            paymentMethod = "Thẻ tín dụng",
            serviceType = ServiceType.PET,
            isCompleted = true
        ),
        Order(
            orderId = "HD009",
            restaurantName = "Guardian",
            restaurantImage = "https://example.com/guardian.jpg",
            orderItems = listOf(
                OrderItem(id = "11", name = "Kem chống nắng", quantity = 1, price = 250000.0, imageUrl = "https://example.com/sunscreen.jpg")
            ),
            totalPrice = 250000.0,
            orderDate = System.currentTimeMillis() - 604800000,
            status = OrderStatus.DELIVERED,
            deliveryAddress = "56 Lê Thánh Tôn, Q.1, TP.HCM",
            paymentMethod = "Momo",
            serviceType = ServiceType.MEDICINE,
            isCompleted = true
        )
    )

    val tabs = listOf(
        "Đang đến",
        "Deal đã mua",
        "Lịch sử",
        "Đánh giá"
    )
    var selectedService by remember { mutableStateOf(ServiceType.ALL) }
    var selectedCompletion by remember { mutableStateOf("Tất cả") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            contentColor = Color(0xFFFF5722),
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color(0xFFFF5722)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) Color(0xFFFF5722) else Color.Gray,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }


        if (selectedTabIndex == 2) {
            OrderFilterBar(
                selectedService = selectedService,
                selectedCompletion = selectedCompletion,
                startDate = startDate,
                endDate = endDate,
                onServiceSelected = { selectedService = it },
                onCompletionSelected = { selectedCompletion = it },
                onDateRangeSelected = { start, end ->
                    startDate = start
                    endDate = end
                }
            )
        }

        OrderHistoryContent(
            orders = testOrders,
            selectedTabIndex = selectedTabIndex,
            selectedService = selectedService,
            selectedCompletion = selectedCompletion,
            startDate = startDate,
            endDate = endDate,
            modifier = Modifier.weight(1f)
        )
    }
}



@Composable
fun OrderFilterBar(
    selectedService: ServiceType,
    selectedCompletion: String,
    startDate: Long?,
    endDate: Long?,
    onServiceSelected: (ServiceType) -> Unit,
    onCompletionSelected: (String) -> Unit,
    onDateRangeSelected: (Long?, Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedService by remember { mutableStateOf(false) }
    var expandedCompletion by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Set default date range to last
    LaunchedEffect(Unit) {
        if (startDate == null && endDate == null) {
            val now = System.currentTimeMillis()
            val lastYear = now - (12 * 30 * 24 * 60 * 60 * 1000L)
            onDateRangeSelected(lastYear, now)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Service type filter
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .clickable { expandedService = true }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedService.toDisplayString(),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(
                expanded = expandedService,
                onDismissRequest = { expandedService = false }
            ) {
                ServiceType.entries.forEach { serviceType ->
                    DropdownMenuItem(
                        text = { Text(serviceType.toDisplayString()) },
                        onClick = {
                            onServiceSelected(serviceType)
                            expandedService = false
                        }
                    )
                }
            }
        }

        // Vertical divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(Color.LightGray)
        )

        // Completion status filter
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .clickable { expandedCompletion = true }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedCompletion,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(
                expanded = expandedCompletion,
                onDismissRequest = { expandedCompletion = false }
            ) {
                listOf("Tất cả", "Hoàn thành", "Đã hủy").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onCompletionSelected(option)
                            expandedCompletion = false
                        }
                    )
                }
            }
        }

        // Vertical divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(Color.LightGray)
        )

        // Date range filter
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .clickable { showDatePicker = true }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (startDate != null && endDate != null) {
                        val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
                        "${formatter.format(Date(startDate))} - ${formatter.format(Date(endDate))}"
                    } else "Chọn ngày",
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        if (showDatePicker) {
            DateRangePicker(
                onDismiss = { showDatePicker = false },
                onDateSelected = { start, end ->
                    onDateRangeSelected(start, end)
                    showDatePicker = false
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePicker(
    onDismiss: () -> Unit,
    onDateSelected: (Long, Long) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()
    val orangeColor = Color(0xFFFF5722)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    dateRangePickerState.selectedStartDateMillis?.let { start ->
                        dateRangePickerState.selectedEndDateMillis?.let { end ->
                            onDateSelected(start, end)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = orangeColor,
                    contentColor = Color.White
                ),
                enabled = dateRangePickerState.selectedStartDateMillis != null &&
                        dateRangePickerState.selectedEndDateMillis != null
            ) {
                Text("Chọn")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, orangeColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = orangeColor
                )
            ) {
                Text("Xóa")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Chọn khoảng thời gian",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier
                    .height(400.dp)
                    .fillMaxWidth(),
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = orangeColor,
                    todayDateBorderColor = orangeColor,
                    todayContentColor = orangeColor,
                    selectedDayContentColor = Color.White,
                    dayInSelectionRangeContainerColor = orangeColor.copy(alpha = 0.2f),
                    dayInSelectionRangeContentColor = orangeColor
                ),
                title = null
            )
        }
    }
}

@Composable
fun OrderHistoryContent(
    orders: List<Order>,
    selectedTabIndex: Int,
    selectedService: ServiceType,
    selectedCompletion: String,
    startDate: Long?,
    endDate: Long?,
    modifier: Modifier = Modifier
) {
    val filteredOrders = orders.filter { order ->
        when (selectedTabIndex) {
            0 -> order.status == OrderStatus.UPCOMING || order.status == OrderStatus.IN_PROGRESS
            2 -> {
                val matchesService = selectedService == ServiceType.ALL || order.serviceType == selectedService
                val matchesCompletion = when (selectedCompletion) {
                    "Hoàn thành" -> order.isCompleted && order.status == OrderStatus.DELIVERED
                    "Đã hủy" -> order.status == OrderStatus.CANCELLED
                    else -> order.status == OrderStatus.DELIVERED || order.status == OrderStatus.CANCELLED
                }
                val matchesDate = if (startDate != null && endDate != null) {
                    order.orderDate in startDate..endDate
                } else true

                matchesService && matchesCompletion && matchesDate
            }
            else -> false
        }
    }

    if (filteredOrders.isEmpty()) {
        EmptyOrderState(modifier)
    } else {
        OrderList(filteredOrders, modifier)
    }
}

@Composable
private fun EmptyOrderState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ListAlt,
            contentDescription = "Empty Order",
            modifier = Modifier.size(120.dp),
            tint = Color(0xFFFF5722)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chưa có đơn nào",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Gray
        )
    }
}

@Composable
private fun OrderList(
    orders: List<Order>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(orders) { order ->
            OrderCard(order)
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = order.restaurantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.status.toDisplayString(),
                    color = order.status.toColor()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Đơn hàng: ${order.orderId}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tổng tiền: ${formatPrice(order.totalPrice)}đ",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Color(0xFFFF5722)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF5722)
                    )
                ) {
                    Text("Hỗ trợ")
                }

                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5722),
                        contentColor = Color.White
                    )
                ) {
                    Text("Đặt lại")
                }
            }
        }
    }
}

// Helper functions
private fun OrderStatus.toDisplayString(): String = when(this) {
    OrderStatus.UPCOMING -> "Đang đến"
    OrderStatus.IN_PROGRESS -> "Đang xử lý"
    OrderStatus.DELIVERED -> "Đã giao"
    OrderStatus.CANCELLED -> "Đã hủy"
}

private fun OrderStatus.toColor(): Color = when(this) {
    OrderStatus.UPCOMING -> Color(0xFFFF5722)
    OrderStatus.IN_PROGRESS -> Color(0xFF2196F3)
    OrderStatus.DELIVERED -> Color(0xFF4CAF50)
    OrderStatus.CANCELLED -> Color.Gray
}

private fun formatPrice(price: Double): String {
    return String.format("%,.0f", price)
}

@Preview(showBackground = true)
@Composable
fun OrderHistoryScreenPreview() {
    OrderHistoryScreen()
}