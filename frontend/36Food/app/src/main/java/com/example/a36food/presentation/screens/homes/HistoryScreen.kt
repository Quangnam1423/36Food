package com.example.a36food.presentation.screens.homes

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a36food.Screen
import com.example.a36food.domain.model.Order
import com.example.a36food.ui.components.BottomNavBar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {

    var Orders = remember { mutableStateListOf<Order>() }

    Scaffold(
        topBar = {
            HistoryTopAppBar(onSearchClick = onNavigateToSearch)

        },
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
        HistoryContent(
            modifier = Modifier.padding(paddingValues)
        )
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

@Composable
private fun HistoryContent(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Đang đến", "Lịch sử", "Đánh giá", "Đơn nháp")

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
                    onClick = { selectedTab = index },
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
            0 -> OngoingOrdersSection()
            1 -> HistorySection()
            2 -> ReviewsSection()
            3-> DraftOrderSection()
        }
    }
}

@Composable
private fun OngoingOrdersSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Đang đến",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        EmptyStateMessage(
            icon = Icons.Default.Restaurant,
            title = "Quên chưa đặt món rồi nè bạn ơi?",
            description = "Bạn sẽ nhìn thấy các món đang được chuẩn bị hoặc giao đi tại đây để kiểm tra đơn hàng nhanh hơn!"
        )
    }
}

@Composable
private fun HistorySection() {
    var selectedServiceType by remember { mutableStateOf("Tất cả") }
    var selectedStatus by remember { mutableStateOf("Tất cả") }
    val currentDate = Calendar.getInstance()
    var startDate by remember { mutableStateOf(currentDate.apply { add(Calendar.YEAR, -1) }.time) }
    var endDate by remember { mutableStateOf(Calendar.getInstance().time) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Lịch sử",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        FilterSection(
            selectedServiceType = selectedServiceType,
            onServiceTypeSelected = { selectedServiceType = it },
            selectedStatus = selectedStatus,
            onStatusSelected = { selectedStatus = it },
            startDate = startDate,
            endDate = endDate,
            onDateRangeSelected = { start, end ->
                if (start != null) startDate = start
                if (end != null) endDate = end
            }
        )

        EmptyStateMessage(
            icon = Icons.AutoMirrored.Filled.List,
            title = "Chưa có đơn hàng nào",
            description = "Hãy đặt món ăn và theo dõi lịch sử đơn hàng của bạn tại đây nhé"
        )
    }
}

@Composable
private fun FilterSection(
    selectedServiceType: String,
    onServiceTypeSelected: (String) -> Unit,
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    startDate: Date,
    endDate: Date,
    onDateRangeSelected: (Date?, Date?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterDropdownButton(
                    selected = selectedServiceType,
                    options = listOf("Tất cả", "Đồ uống", "Rượu", "Tạp hóa"),
                    onOptionSelected = onServiceTypeSelected,
                    modifier = Modifier.weight(1f)
                )

                FilterDropdownButton(
                    selected = selectedStatus,
                    options = listOf("Tất cả", "Hoàn thành", "Đã hủy"),
                    onOptionSelected = onStatusSelected,
                    modifier = Modifier.weight(1f)
                )

                DateRangeButton(
                    startDate = startDate,
                    endDate = endDate,
                    onDateRangeSelected = onDateRangeSelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}



@Composable
private fun FilterDropdownButton(
    selected: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF666666)
            )
        ) {
            Text(
                text = selected,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangeButton(
    startDate: Date,
    endDate: Date,
    onDateRangeSelected: (Date?, Date?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    // Thay đổi format để hiển thị cả năm
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF666666)
            )
        ) {
            Text(
                text = "${dateFormatter.format(startDate)} - ${dateFormatter.format(endDate)}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.DateRange, null)
        }

        if (showDatePicker) {
            val dateRangePickerState = rememberDateRangePickerState(
                initialSelectedStartDateMillis = startDate.time,
                initialSelectedEndDateMillis = endDate.time,
                yearRange = IntRange(2023, 2025),
                initialDisplayMode = DisplayMode.Picker
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dateRangePickerState.selectedStartDateMillis?.let { startMillis ->
                                dateRangePickerState.selectedEndDateMillis?.let { endMillis ->
                                    onDateRangeSelected(
                                        Date(startMillis),
                                        Date(endMillis)
                                    )
                                }
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Xác nhận")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Hủy")
                    }
                }
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier.height(height = 500.dp),
                    showModeToggle = false,
                    title = {
                        Text(
                            text = "Chọn khoảng thời gian",
                            modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                        )
                    },
                    headline = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 12.dp, top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box {
                                if (dateRangePickerState.selectedStartDateMillis != null) {
                                    Text(
                                        text = "Từ: ${dateFormatter.format(Date(dateRangePickerState.selectedStartDateMillis!!))}",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                            Box {
                                if (dateRangePickerState.selectedEndDateMillis != null) {
                                    Text(
                                        text = "Đến: ${dateFormatter.format(Date(dateRangePickerState.selectedEndDateMillis!!))}",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ReviewsSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Đánh giá",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        EmptyStateMessage(
            icon = Icons.Default.Star,
            title = "Chưa có đánh giá nào",
            description = "Hãy đặt món ăn và cho mọi người biết ý kiến của bạn nhé"
        )
    }
}

@Composable
private fun DraftOrderSection(

) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Đơn nháp",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        EmptyStateMessage(
            icon = Icons.Default.Description,
            title = "Chưa có đơn nháp nào",
            description = "Đơn hàng của bạn sẽ được lưu tạm thời tại đây để tiếp tục đặt hàng sau"
        )
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

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen()
}