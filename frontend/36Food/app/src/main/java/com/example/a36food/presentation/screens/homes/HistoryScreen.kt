package com.example.a36food.presentation.screens.homes

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a36food.Screen
import com.example.a36food.data.dto.OrderResponseDTO
import com.example.a36food.domain.model.Review
import com.example.a36food.presentation.viewmodel.HistoryViewModel
import com.example.a36food.ui.components.BottomNavBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.ImeAction

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToOrderDetail: (Long) -> Unit = {},
    onNetworkError: () -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedTab = remember { mutableIntStateOf(0) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isLoading)

    // Set up network error handler and navigation handler
    LaunchedEffect(Unit) {
        viewModel.setNetworkErrorHandler {
            onNetworkError()
        }
        viewModel.setNavigationHandler { orderId ->
            onNavigateToOrderDetail(orderId)
        }

        // Load processing orders first since we start on the first tab
        viewModel.loadProcessingOrders()

        // Load other orders as well
        viewModel.loadOrders()
    }

    // Update orders when tab changes
    LaunchedEffect(selectedTab.intValue) {
        when (selectedTab.intValue) {
            0 -> viewModel.loadProcessingOrders()
            1 -> viewModel.loadOrders(null)
            2 -> viewModel.loadOrders(null) // All orders for review tab
            3 -> viewModel.loadOrders("DRAFT")
        }
    }

    // Show error in snackbar if there is one
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            HistoryTopAppBar(onSearchClick = onNavigateToSearch)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(
                selectedRoute = Screen.History.route,
                onNavigateToHome = onNavigateToHome,
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToFavorite = onNavigateToFavorite,
                onNavigateToHistory = {},
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                when (selectedTab.intValue) {
                    0 -> viewModel.loadProcessingOrders()
                    1 -> viewModel.loadOrders("COMPLETED")
                    2 -> viewModel.loadOrders(null) // All orders for review tab
                    3 -> viewModel.loadOrders("DRAFT")
                }
            }
        ) {
            HistoryContent(
                selectedTab = selectedTab.intValue,
                onTabSelected = { tab ->
                    selectedTab.intValue = tab
                    when (tab) {
                        0 -> viewModel.loadProcessingOrders()
                        1 -> viewModel.loadOrders("COMPLETED")
                        2 -> viewModel.loadOrders(null) // All orders for review tab
                        3 -> viewModel.loadOrders("DRAFT")
                    }
                },
                isLoading = state.isLoading,
                ongoingOrders = viewModel.getOrdersByStatus("ONGOING"),
                completedOrders = viewModel.getOrdersByStatus("COMPLETED"),
                allOrders = state.orders,
                draftOrders = viewModel.getOrdersByStatus("DRAFT"),
                onOrderClick = onNavigateToOrderDetail,
                modifier = Modifier.padding(paddingValues),
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopAppBar(
    onSearchClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Đơn hàng",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp
                ),
                color = Color.White
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFF5722),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HistoryContent(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isLoading: Boolean,
    ongoingOrders: List<OrderResponseDTO>,
    completedOrders: List<OrderResponseDTO>,
    allOrders: List<OrderResponseDTO>,
    draftOrders: List<OrderResponseDTO>,
    onOrderClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val tabs = listOf("Đang đến", "Lịch sử", "Đánh giá", "Đơn nháp")

    // Toggle review mode when tab changes
    LaunchedEffect(selectedTab) {
        viewModel.toggleReviewMode(selectedTab == 2)
    }

    Column(modifier = modifier) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Color(0xFFFF5722),
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 2.dp,
                    color = Color(0xFFFF5722)
                )
            },
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selectedTab == index) {
                                Color(0xFFFF5722)
                            } else {
                                Color.Gray
                            }
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> OrdersSection(
                title = "Đang đến",
                orders = viewModel.getProcessingOrders(),
                isLoading = isLoading,
                emptyIcon = Icons.Default.Restaurant,
                emptyTitle = "Quên chưa đặt món rồi nè bạn ơi?",
                emptyDescription = "Bạn sẽ nhìn thấy các món đang được chuẩn bị hoặc giao đi tại đây để kiểm tra đơn hàng nhanh hơn!",
                onOrderClick = onOrderClick,
                viewModel = viewModel
            )
            1 -> OrdersSection(
                title = "Lịch sử đơn hàng",
                orders = if (state.selectedStatus != null) viewModel.getHistoryOrders() else completedOrders,
                isLoading = isLoading,
                emptyIcon = Icons.AutoMirrored.Filled.List,
                emptyTitle = "Chưa có đơn hàng nào",
                emptyDescription = "Hãy đặt món ăn và theo dõi lịch sử đơn hàng của bạn tại đây nhé",
                showFilter = true,
                onOrderClick = onOrderClick,
                viewModel = viewModel
            )
            2 -> ReviewsSection(
                viewModel = viewModel,
                isLoading = state.isLoadingReviews
            )
            3 -> OrdersSection(
                title = "Đơn nháp",
                orders = draftOrders,
                isLoading = isLoading,
                emptyIcon = Icons.Default.Description,
                emptyTitle = "Chưa có đơn nháp nào",
                emptyDescription = "Đơn hàng của bạn sẽ được lưu tạm thời tại đây để tiếp tục đặt hàng sau",
                onOrderClick = onOrderClick
            )
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun OrdersSection(
    title: String,
    orders: List<OrderResponseDTO>,
    isLoading: Boolean,
    emptyIcon: ImageVector,
    emptyTitle: String,
    emptyDescription: String,
    showFilter: Boolean = false,
    onOrderClick: (Long) -> Unit,
    viewModel: HistoryViewModel? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp),
            color = Color(0xFFFF5722),
        )

        // Filter section if needed
        if (showFilter && viewModel != null) {
            val state by viewModel.state.collectAsState()
            FilterSection(
                viewModel = viewModel,
                selectedStatus = state.selectedStatus,
                startDate = state.startDate,
                endDate = state.endDate
            )
        }

        // Content
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFF5722))
            }
        } else if (orders.isEmpty()) {
            EmptyStateMessage(
                icon = emptyIcon,
                title = emptyTitle,
                description = emptyDescription
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order = order, onClick = { onOrderClick(order.orderId) }, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(
    icon: ImageVector,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color(0xFFFF5722)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun OrderCard(
    order: OrderResponseDTO,
    onClick: () -> Unit,
    viewModel: HistoryViewModel? = null
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val statusColor = when (order.status) {
        "PENDING" -> Color(0xFFFF9800) // Orange
        "PREPARING" -> Color(0xFF2196F3) // Blue
        "COMPLETED" -> Color(0xFF4CAF50) // Green
        "CANCELLED" -> Color(0xFFF44336) // Red
        "DRAFT" -> Color(0xFF9E9E9E) // Gray
        null -> Color.Gray // Handle null status
        else -> Color.Gray
    }

    // State for dialog visibility
    var showCancelDialog by remember { mutableStateOf(false) }
    var showReorderDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    // Context for location
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Order ID and Date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Đơn hàng #${order.orderId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = dateFormatter.format(order.orderDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Status chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = when(order.status) {
                            "PENDING" -> "Đang chờ"
                            "PREPARING" -> "Đang chuẩn bị"
                            "COMPLETED" -> "Hoàn thành"
                            "CANCELLED" -> "Đã hủy"
                            "DRAFT" -> "Bản nháp"
                            null -> "Không xác định"
                            else -> order.status
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            // Order items summary
            Text(
                text = "Tổng ${order.items.size} món",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Show a few order items
            order.items.take(2).forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.quantity}x ${item.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${item.price * item.quantity} đ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            // If there are more items, show an indicator
            if (order.items.size > 2) {
                Text(
                    text = "...và ${order.items.size - 2} món khác",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            // Total amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng cộng",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${order.totalAmount} đ",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFFF5722),
                    fontWeight = FontWeight.Bold
                )
            }

            // Show buttons based on order status
            if (viewModel != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Show cancel button only for PENDING orders
                    if (order.status == "PENDING") {
                        Button(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF44336) // Red color for cancel button
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Order",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hủy đơn hàng", color = Color.White)
                        }
                    }

                    // Show add review and reorder buttons for COMPLETED orders
                    if (order.status == "COMPLETED") {
                        // Add Review button
                        Button(
                            onClick = { showReviewDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800) // Orange color for review button
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Add Review",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thêm Đánh giá", color = Color.White)
                        }

                        // Reorder button
                        Button(
                            onClick = { showReorderDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50) // Green color for reorder button
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reorder",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Đặt lại", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Cancel confirmation dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Xác nhận hủy đơn hàng") },
            text = { Text("Bạn có chắc chắn muốn hủy đơn hàng #${order.orderId} không?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel?.cancelOrder(order.orderId)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336) // Red color for confirm button
                    )
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCancelDialog = false }
                ) {
                    Text("Hủy bỏ")
                }
            }
        )
    }

    // Reorder confirmation dialog
    if (showReorderDialog) {
        AlertDialog(
            onDismissRequest = { showReorderDialog = false },
            title = { Text("Xác nhận đặt lại đơn hàng") },
            text = {
                Column {
                    Text("Bạn muốn đặt lại đơn hàng #${order.orderId}?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Chúng tôi sẽ sử dụng vị trí hiện tại của bạn để tính phí vận chuyển.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Lưu ý: Bạn có thể chỉnh sửa đơn hàng trước khi xác nhận.",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel?.reorderOrder(order.orderId)
                        showReorderDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50) // Green color for confirm button
                    )
                ) {
                    Text("Đặt lại")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showReorderDialog = false }
                ) {
                    Text("Hủy bỏ")
                }
            }
        )
    }

    // Add Review confirmation dialog
    if (showReviewDialog) {
        ReviewFormDialog(
            order = order,
            onDismiss = { showReviewDialog = false },
            onSubmit = { restaurantId, content, rating, isAnonymous, imageUrls, foodId, orderId ->
                viewModel?.submitReview(
                    restaurantId = restaurantId,
                    content = content,
                    rating = rating,
                    isAnonymous = isAnonymous,
                    imageUrls = imageUrls,
                    foodId = foodId,
                    orderId = orderId
                )
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    viewModel: HistoryViewModel,
    selectedStatus: String? = null,
    startDate: String? = null,
    endDate: String? = null
) {
    val statusOptions = listOf("Tất cả", "Hoàn thành", "Đã hủy")
    var selectedStatusIndex by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isStartDateSelection by remember { mutableStateOf(true) }

    // Định dạng ngày tháng để hiển thị
    val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    // Parse các ngày từ ISO format để hiển thị
    val startDateDisplay = startDate?.let {
        try {
            apiDateFormat.parse(it)?.let { date ->
                displayDateFormat.format(date)
            } ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    } ?: "N/A"

    val endDateDisplay = endDate?.let {
        try {
            apiDateFormat.parse(it)?.let { date ->
                displayDateFormat.format(date)
            } ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    } ?: "N/A"

    // Khi component khởi tạo, chọn đúng tab dựa trên status hiện tại
    LaunchedEffect(selectedStatus) {
        selectedStatusIndex = when(selectedStatus) {
            "COMPLETED" -> 1
            "CANCELLED" -> 2
            else -> 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Bộ lọc trạng thái
        Text(
            text = "Trạng thái đơn hàng",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            statusOptions.forEachIndexed { index, label ->
                FilterChip(
                    selected = selectedStatusIndex == index,
                    onClick = {
                        selectedStatusIndex = index
                        val newStatus = when(index) {
                            1 -> "COMPLETED"
                            2 -> "CANCELLED"
                            else -> null
                        }
                        viewModel.updateStatusFilter(newStatus)
                    },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF5722).copy(alpha = 0.1f),
                        selectedLabelColor = Color(0xFFFF5722)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bộ lọc khoảng thời gian
        Text(
            text = "Khoảng thời gian",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chọn ngày bắt đầu
            OutlinedCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        isStartDateSelection = true
                        showDatePicker = true
                    },
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color.White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Từ ngày",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = startDateDisplay,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFF5722) // Added darker text color for better contrast
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Chọn ngày bắt đầu",
                        tint = Color(0xFFFF5722)
                    )
                }
            }

            // Chọn ngày kết thúc
            OutlinedCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        isStartDateSelection = false
                        showDatePicker = true
                    },
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color.White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Đến ngày",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = endDateDisplay,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF333333) // Added darker text color for better contrast
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Chọn ngày kết thúc",
                        tint = Color(0xFFFF5722)
                    )
                }
            }
        }
    }

    // Dialog chọn ngày - Thay thế DatePickerDialog bằng Dialog tùy chỉnh
    if (showDatePicker) {
        val calendar = Calendar.getInstance()

        // Đặt ngày ban đầu
        if (isStartDateSelection && startDate != null) {
            try {
                apiDateFormat.parse(startDate)?.let { date ->
                    calendar.time = date
                }
            } catch (e: Exception) {
                calendar.add(Calendar.MONTH, -6) // Mặc định 6 tháng trước
            }
        } else if (!isStartDateSelection && endDate != null) {
            try {
                apiDateFormat.parse(endDate)?.let { date ->
                    calendar.time = date
                }
            } catch (e: Exception) {
                // Để mặc định là ngày hiện tại
            }
        } else if (isStartDateSelection) {
            calendar.add(Calendar.MONTH, -6) // Mặc định 6 tháng trước
        }

        // Sử dụng lazy để khởi tạo datePickerState sau khi calendar đã được thiết lập
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = calendar.timeInMillis
        )

        Dialog(onDismissRequest = { showDatePicker = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isStartDateSelection) "Chọn ngày bắt đầu" else "Chọn ngày kết thúc",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // SimpleDatePicker không cần các tham số không hỗ trợ
                    DatePicker(
                        state = datePickerState,
                        title = null,
                        headline = null
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showDatePicker = false }
                        ) {
                            Text("Hủy")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                val timeInMillis = datePickerState.selectedDateMillis
                                if (timeInMillis != null) {
                                    val selectedDate = java.time.Instant.ofEpochMilli(timeInMillis)
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate()

                                    val time = if (isStartDateSelection) "00:00:00" else "23:59:59"
                                    val formattedDate = "${selectedDate}T$time"

                                    if (isStartDateSelection) {
                                        // Cập nhật ngày bắt đầu
                                        viewModel.updateDateRange(formattedDate, endDate ?: "")
                                    } else {
                                        // Cập nhật ngày kết thúc
                                        viewModel.updateDateRange(startDate ?: "", formattedDate)
                                    }
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("Xác nhận")
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ReviewsSection(
    viewModel: HistoryViewModel,
    isLoading: Boolean
) {
    val state by viewModel.state.collectAsState()
    val reviews = state.userReviews

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title with rating summary
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Đánh giá của bạn",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFF5722)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rating summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trung bình",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format("%.1f", state.averageRating),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5722)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        RatingBar(rating = state.averageRating)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${state.totalReviewCount} đánh giá",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        // Reviews content
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFF5722))
            }
        } else if (reviews.isEmpty()) {
            EmptyStateMessage(
                icon = Icons.Default.Star,
                title = "Bạn chưa có đánh giá nào",
                description = "Hãy đặt món và đánh giá để chia sẻ trải nghiệm của bạn"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reviews) { review ->
                    ReviewCard(review = review)
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(
    review: Review
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(review.createdAt)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with date and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                RatingBar(rating = review.rating)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Food name if available (using resourceId as placeholder)
            Text(
                text = "Đánh giá món ăn ID: ${review.foodId ?: "Không có thông tin"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (review.restaurantId != null) {
                Text(
                    text = "Nhà hàng ID: ${review.restaurantId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Review content
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium
            )

            // Review images if available
            if (review.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    review.imageUrls.take(3).forEach { imageUrl ->
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

                    // Show "more" indicator if there are more than 3 images
                    if (review.imageUrls.size > 3) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${review.imageUrls.size - 3}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order ID if available
            if (review.orderId != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Đơn hàng #${review.orderId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF5722)
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingBar(
    rating: Float,
    starSize: Dp = 16.dp
) {
    Row {
        repeat(5) { index ->
            val starIcon = when {
                index < rating.toInt() -> Icons.Default.Star
                index == rating.toInt() && rating % 1 > 0 -> Icons.Default.StarHalf
                else -> Icons.Default.StarOutline
            }

            Icon(
                imageVector = starIcon,
                contentDescription = null,
                tint = Color(0xFFFFB74D),
                modifier = Modifier.size(starSize)
            )
        }
    }
}

@Composable
private fun ReviewFormDialog(
    order: OrderResponseDTO,
    onDismiss: () -> Unit,
    onSubmit: (String, String, Float, Boolean, List<String>, String, String) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(5f) }
    var isAnonymous by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf("") }
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

    // For simplicity, we'll use the first food item's details
    val firstItem = order.items.firstOrNull()
    // Ensure foodId is not empty and is a valid value
    val foodId = firstItem?.id?.toString() ?: "0"
    // Ensure restaurantId is not empty
    val restaurantId = order.restaurantId?.toString() ?: "0"
    // Ensure orderId is not empty
    val orderId = order.orderId.toString()

    // Create a focus manager for improved keyboard handling
    val focusManager = LocalFocusManager.current

    // Set up for keyboard behavior
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Đánh giá đơn hàng #${order.orderId}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Food item preview
                if (firstItem != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(firstItem.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = firstItem.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = firstItem.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Số lượng: ${firstItem.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Rating selection
                Text(
                    text = "Đánh giá của bạn",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    RatingSelectionBar(
                        currentRating = rating,
                        onRatingChanged = { rating = it },
                        starSize = 36.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Review content with improved keyboard handling
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Nội dung đánh giá") },
                    placeholder = { Text("Chia sẻ cảm nhận của bạn về món ăn...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Image URLs (optional)
                Text(
                    text = "Thêm ảnh (tùy chọn)",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("URL hình ảnh") },
                        placeholder = { Text("https://example.com/image.jpg") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (imageUrl.isNotBlank()) {
                                    imageUrls = imageUrls + imageUrl
                                    imageUrl = ""
                                }
                                focusManager.clearFocus()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (imageUrl.isNotBlank()) {
                                imageUrls = imageUrls + imageUrl
                                imageUrl = ""
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Image URL",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Show added image URLs
                if (imageUrls.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(imageUrls) { url ->
                            Box(modifier = Modifier.size(80.dp)) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(url)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Review image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )

                                IconButton(
                                    onClick = {
                                        imageUrls = imageUrls.filter { it != url }
                                    },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.TopEnd)
                                        .background(
                                            color = Color(0xFFF44336),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Image",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Anonymous option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAnonymous = !isAnonymous }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAnonymous,
                        onCheckedChange = { isAnonymous = it }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Đánh giá ẩn danh",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Hủy")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // Validate all fields before submission
                            if (content.isNotBlank() && rating > 0) {
                                onSubmit(
                                    restaurantId,
                                    content,
                                    rating,
                                    isAnonymous,
                                    imageUrls,
                                    foodId,
                                    orderId
                                )
                                onDismiss()
                            }
                        },
                        enabled = content.isNotBlank() && rating > 0
                    ) {
                        Text("Gửi đánh giá")
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingSelectionBar(
    currentRating: Float,
    onRatingChanged: (Float) -> Unit,
    starSize: Dp = 48.dp
) {
    Row {
        repeat(5) { index ->
            val position = index + 1
            val isFilled = position <= currentRating

            IconButton(
                onClick = { onRatingChanged(position.toFloat()) },
                modifier = Modifier.size(starSize)
            ) {
                Icon(
                    imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = "Rate $position",
                    tint = Color(0xFFFFB74D),
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}
