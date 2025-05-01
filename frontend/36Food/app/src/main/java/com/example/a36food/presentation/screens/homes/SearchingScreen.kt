package com.example.a36food.presentation.screens.homes

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.a36food.ui.components.CartIcon
import com.example.a36food.ui.components.RoundedIconButton
import com.example.a36food.ui.components.BottomNavBar
import com.example.a36food.R
import com.example.a36food.Screen
import com.example.a36food.data.local.RecentSearchManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchingScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToFavorite: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onSearchClick: (String) -> Unit = {},
    onCartClick: () -> Unit = {}
) {

    var searchQuery by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold (
        topBar = {
            SearchingTopAppBar(
                onBackClick = onNavigateToHome,
                onCartClick = {/*TO SEE CART DETAIL*/}
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedRoute = Screen.Search.route,
                onNavigateToHome = onNavigateToHome,
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToFavorite = onNavigateToFavorite,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        SearchingLayout(
            Modifier.padding(paddingValues),
            searchQuery = searchQuery,
            onSearchingChanged = {},
            onSearchClick = {},
            onViewAll = {}
        )
    }
}

@Composable
fun SearchingTopAppBar(
    onCartClick: () -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        backgroundColor = colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoundedIconButton(
                onClick = {onBackClick},
                icon = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Back"
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_medium)))
            Text (
                text = "Tìm Kiếm",
                style = androidx.compose.material.MaterialTheme.typography.h4,
                color = Color.Green
            )
            Spacer(modifier = Modifier.weight(1f))
            CartIcon(
                cartCount = 1, // change after that
                onClick = {onCartClick}
            )
        }
    }
}

@Composable
fun SearchingLayout(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchingChanged: (String) -> Unit,
    onSearchClick: (String) ->Unit,
    onViewAll: () -> Unit,
) {
    val context = LocalContext.current
    var recentSearches by remember {mutableStateOf(listOf("Bánh Mì", "Sushi", "Trà Sữa", "Pizza", "Phở", "Bún Bò", "Cà Phê"))}



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchQuery,
            singleLine = true,
            shape = shapes.large,
            trailingIcon = {Icon(Icons.Default.Search,contentDescription = "Search")},
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                disabledContainerColor = colorScheme.surface,
            ),
            onValueChange = {onSearchingChanged},
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {/*TO DO*/}
            )
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Tìm Kiếm Gần Đây",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                color = Color(0xFFFF9800),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        RecentSearchManager.clearSearches(context)
                    }
                }
            ) {
                Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.Black)
            }
        }
        RecentSearchGrid(
            onSearchClick = {},
            recentSearches = recentSearches
        )

        SuggestionList()
    }
}

@Composable
fun RecentSearchGrid(
    onSearchClick: (String) -> Unit,
    recentSearches: List<String>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.height(100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(recentSearches) { keyword ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .clickable { onSearchClick(keyword) }
            ) {
                Text(
                    text = keyword,
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SuggestionList(
    modifier: Modifier = Modifier,

    ) {

    val suggestions = listOf(
        Pair("Pizza", R.drawable.pizza_suggestion),
        Pair("Nem cuốn", R.drawable.nem_cuon_suggestion),
        Pair("Thịt ba chỉ", R.drawable.thit_ba_chi_suggestion),
        Pair("Steak", R.drawable.steak_suggestion),
        Pair("Soda", R.drawable.soda_suggestion),
        Pair("Tôm hùm", R.drawable.tom_hum_suggestion),
        Pair("Bò Wellington", R.drawable.bo_wellington_suggestion),
        Pair("Bánh Mì", R.drawable.banh_mi_suggestion),
        Pair("Coca-Cola", R.drawable.coca_cola_suggestion)
    )
    Column(
        modifier = modifier
            .padding(16.dp),
    ) {
        Text(
            text = "Gợi Ý Cho Bạn",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            color = Color(0xFFFF9800),
            textAlign = TextAlign.Start
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(400.dp)
        ) {
            items(suggestions) { (name, imageRes) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Green,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchingScreenPreview (

) {
    SearchingScreen()
}