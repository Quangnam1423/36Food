package com.example.a36food.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.a36food.R

@Composable
fun HomeTopAppBar() {
    TopAppBar(
        backgroundColor = colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoundedIconButton(
                onClick = {/*TO DO*/},
                icon = Icons.Default.Menu,
                contentDescription = "Menu"
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))

            Column(
            ){
                Text("Giao hàng đến",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Green
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                Text("Yên Xá, Tân Triều, Thanh Trì...",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFF5722),
                    maxLines = 1,
                    modifier = Modifier
                        .clickable { /* TO DO*/ }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { /* open search */ }
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeTopAppBarPreView() {
    HomeTopAppBar()
}