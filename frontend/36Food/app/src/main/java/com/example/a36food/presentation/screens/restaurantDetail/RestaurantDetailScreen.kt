package com.example.a36food.presentation.screens.restaurantDetail

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a36food.R
import com.example.a36food.domain.model.BusinessHours
import com.example.a36food.domain.model.FoodItem
import com.example.a36food.domain.model.OpeningStatus
import com.example.a36food.domain.model.Restaurant
import com.example.a36food.domain.model.ServiceType
import com.example.a36food.ui.components.RoundedIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen (
    onBackClick: () -> Unit,
    onShareClick: () -> Unit = {},
    onFoodClick: (String) -> Unit = {},
    onAddClick: (FoodItem) -> Unit = {},
    restaurantId: String,

) {
    var query by remember {mutableStateOf("")}
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item{
                RestaurantDetailHeader(
                    restaurant = createRestaurantSample(restaurantId),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item{
                Text(
                    text = "Danh sách các món ăn của nhà hàng",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(generateSampleFoodItems()) { foodItem ->
                FoodItemCard(
                    foodItem = foodItem,
                    onAddClick = onAddClick,
                    onFoodClick = onFoodClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        RestaurantDetailAppBar(
            restaurantName = "Restaurant Name",
            query = query,
            onQueryChanged = { query = it },
            onBackClick = onBackClick,
            onShareClick = onShareClick,
            scrollBehavior = scrollBehavior,
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.TopStart)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  RestaurantDetailAppBar(
    restaurantName: String,
    query: String,
    onQueryChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            OutlinedTextField(
                value = query,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.padding(
                    horizontal = 2.dp, vertical = 2.dp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.9f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                    disabledContainerColor = Color.White.copy(alpha = 0.9f),
                ),
                onValueChange = { onQueryChanged(it) },
                trailingIcon = {
                    IconButton(
                        onClick = {/*TO DO*/ }
                    ) {
                        Icon(imageVector = Icons.Default.Cancel, contentDescription = "hidden Icon")
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {/*TO DO*/ }
                )
            )
        },
        navigationIcon = {
            RoundedIconButton(
                onClick = onBackClick,
                icon = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back"
            )
        },
        actions = {
            RoundedIconButton(
                onClick = onShareClick,
                icon = Icons.Default.Share,
                contentDescription = "Share",
            )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.White.copy(alpha = 0.9f)
        ),
        modifier = modifier
    )
}

@Composable
private fun RestaurantDetailHeader(
    restaurant: Restaurant,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current).data(restaurant.imageUrl)
            .crossfade(true)
            .build(),
        error = painterResource(R.drawable.ic_broken_image),
        placeholder = painterResource(R.drawable.loading_img),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )

    Row(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint =  Color(0xFFFFA500)
                )
                Text(" |  10+ bình luận  |  29 phút", modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { /* Handle favorite click */ },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodItemCard(
    modifier: Modifier = Modifier,
    foodItem: FoodItem,
    onAddClick: (FoodItem) -> Unit,
    onFoodClick: (String) -> Unit = {}
) {
    Card(
        modifier = modifier
            .clickable { onFoodClick(foodItem.id) }
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current).data(foodItem.imageUrl)
                    .crossfade(true)
                    .build(),
                error = painterResource(R.drawable.ic_broken_image),
                placeholder = painterResource(R.drawable.loading_img),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${foodItem.saleCount} đã bán",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "  |  ${foodItem.likes} lượt thích",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${foodItem.price.toInt()}đ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { onAddClick(foodItem) },
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .background(Color(0xFFFF5722), shape = CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun createRestaurantSample(restaurantId: String): Restaurant {
    return Restaurant(
        id = restaurantId,
        name = "Phở Thìn Bờ Hồ",
        imageUrl = "https://example.com/pho.jpg", // tạm thời dùng R.drawable khi hiển thị
        rating = 4.5f,
        ratingCount = 234,
        address = "13 Lò Đúc, Hai Bà Trưng, Hà Nội",
        priceRange = "20000",
        openingStatus = OpeningStatus.SCHEDULED,
        businessHours = BusinessHours(
            openTime = "07:00",
            closeTime = "22:00"
        ),
        serviceType = ServiceType.FOOD,
        phoneNumber = "0123456789",
        likes = 156,
        categories = listOf("Phở", "Món Việt", "Đặc sản")
    )
}

private fun generateSampleFoodItems(): List<FoodItem> {
    return listOf(
        FoodItem(
            id = "1",
            name = "Phở bò tái nạm",
            description = "Phở bò với thịt tái và nạm, bánh phở mềm, nước dùng đậm đà",
            price = 45000.0,
            imageUrl = "https://example.com/pho-bo.jpg",
            restaurantId = "1",
            categoryId = "pho",
            isAvailable = true,
            isPopular = true,
            likes = 156,
            saleCount = 1200
        ),
        FoodItem(
            id = "2",
            name = "Phở gà",
            description = "Phở với thịt gà ta chuẩn vị, nước dùng trong và ngọt",
            price = 40000.0,
            imageUrl = "https://example.com/pho-ga.jpg",
            restaurantId = "1",
            categoryId = "pho",
            isAvailable = true,
            isPopular = true,
            likes = 120,
            saleCount = 800
        ),
        FoodItem(
            id = "3",
            name = "Phở bò trứng",
            description = "Phở bò kèm trứng ốp la, hành phi và các gia vị đặc biệt",
            price = 50000.0,
            imageUrl = "https://example.com/pho-trung.jpg",
            restaurantId = "1",
            categoryId = "pho",
            isAvailable = true,
            isPopular = false,
            likes = 89,
            saleCount = 500
        ),
        FoodItem(
            id = "4",
            name = "Quẩy nóng",
            description = "Quẩy giòn rụm ăn kèm phở",
            price = 5000.0,
            imageUrl = "https://example.com/quay.jpg",
            restaurantId = "1",
            categoryId = "side_dish",
            isAvailable = true,
            isPopular = false,
            likes = 45,
            saleCount = 2000
        ),
        FoodItem(
            id = "5",
            name = "Trà đá",
            description = "Trà đá mát lạnh",
            price = 5000.0,
            imageUrl = "https://example.com/tra-da.jpg",
            restaurantId = "1",
            categoryId = "drink",
            isAvailable = true,
            isPopular = false,
            likes = 30,
            saleCount = 1500
        )
    )
}


@Preview(showBackground = true)
@Composable
fun RestaurantDetailScreenPreview() {
    RestaurantDetailScreen(
        onBackClick = {},
        restaurantId = "1"
    )
}

