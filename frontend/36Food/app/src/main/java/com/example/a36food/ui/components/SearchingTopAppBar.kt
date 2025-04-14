package com.example.a36food.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.a36food.R

@Composable
fun SearchingTopAppBar() {
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
                icon = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Back"
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_medium)))
            Text (
                text = "Tìm Kiếm",
                style = MaterialTheme.typography.h4,
                color = Color.Green
            )
            Spacer(modifier = Modifier.weight(1f))
            CartIcon(
                cartCount = 1, // change after that
                onClick = {/*TO DO*/}
            )
        }
    }
}

@Preview
@Composable
fun SearchingTopAppBarPreview() {
    SearchingTopAppBar()
}