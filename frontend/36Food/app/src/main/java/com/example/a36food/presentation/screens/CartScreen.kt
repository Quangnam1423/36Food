package com.example.a36food.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.a36food.domain.model.Cart
import com.example.a36food.domain.model.CartItem
import com.example.a36food.presentation.viewmodel.CartViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit = {},
    onNetworkError: () -> Unit = {},
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Set up network error handler
    LaunchedEffect(Unit) {
        viewModel.setNetworkErrorHandler {
            onNetworkError()
        }
        viewModel.loadCart()
    }

    // Show error in snackbar if there is one
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Show success dialog when order is successful
    if (state.successMessage != null) {
        OrderSuccessDialog(
            message = state.successMessage!!,
            onDismiss = {
                viewModel.clearSuccessMessage()
                onBackClick() // Navigate back after successful order
            }
        )
    }

    val cart = state.cart?.getOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Giỏ hàng",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
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
                    titleContentColor = Color.White
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (cart != null && cart.items.isNotEmpty()) {
                CartBottomBar(
                    cart = cart,
                    onCheckoutClick = {viewModel.createOrder()}
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (cart != null && cart.items.isNotEmpty()) {
                CartContent(
                    cart = cart,
                    onRemoveItem = { itemId ->
                        viewModel.removeCartItem(itemId)
                        scope.launch {
                            snackbarHostState.showSnackbar("Đã xóa món ăn khỏi giỏ hàng")
                        }
                    },
                    onUpdateQuantity = { itemId, quantity ->
                        viewModel.updateCartItemQuantity(itemId, quantity)
                    }
                )
            } else if (cart != null && cart.items.isNullOrEmpty()) {
                EmptyCartMessage(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun CartContent(
    cart: Cart,
    onRemoveItem: (String) -> Unit,
    onUpdateQuantity: (String, Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cart.items) { item ->
            CartItemCard(
                item = item,
                onRemoveItem = { onRemoveItem(item.id) },
                onUpdateQuantity = { quantity -> onUpdateQuantity(item.id, quantity) }
            )
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onRemoveItem: () -> Unit,
    onUpdateQuantity: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                error = painterResource(id = R.drawable.ic_broken_image)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Item details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                item.note?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = "Ghi chú: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatPrice(item.price) + "đ",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                // Quantity controls
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (item.quantity > 1) {
                                onUpdateQuantity(item.quantity - 1)
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease quantity",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${item.quantity}",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) },
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase quantity",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Remove button
            IconButton(
                onClick = onRemoveItem,
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove item",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CartBottomBar(
    cart: Cart,
    onCheckoutClick: () -> Unit
) {
    val totalPrice = cart.items.sumOf { it.totalPrice }
    val orangeColor = Color(0xFFFF5722) // Define orange color

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
// Total section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Added padding to match previous response style
            ) {
                Text(
                    text = "Tổng thanh toán:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = orangeColor // Set text color to orange
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End, // Align price to the right
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatPrice(totalPrice) + "đ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = orangeColor // Set text color to orange
                    )
                }
            }

            Button(
                onClick = onCheckoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = orangeColor // Use orange for button background
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Đặt Hàng",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White // Button text color
                )
            }
        }
    }
}

@Composable
fun EmptyCartMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = Color.LightGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Giỏ hàng của bạn đang trống",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFFF5722)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hãy thêm món ăn từ nhà hàng yêu thích của bạn",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFFF5722),
        )
    }
}

@Composable
fun OrderSuccessDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Success",
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF4CAF50) // Green color for success
            )
        },
        title = {
            Text(
                text = "Thành công!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722) // Orange color to match app theme
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Đã hiểu")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}

fun formatPrice(price: Double): String {
    return "%,.0f".format(price)
}

