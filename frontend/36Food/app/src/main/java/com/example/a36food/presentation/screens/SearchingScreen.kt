package com.example.a36food.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.a36food.ui.components.BottomNavBar


@Composable
fun SearchingScreen(

) {
    Scaffold (
        topBar = {
            SearchingTopAppBar()
        },
        bottomBar = {
            BottomNavBar()
        }
    ) {paddingValues ->
        SearchingLayout(Modifier.padding(paddingValues))

    }
}

@Composable
fun SearchingTopAppBar() {

}

@Composable
fun SearchingLayout (
    modifier: Modifier = Modifier
) {

}

@Preview(showBackground = true)
@Composable
fun SearchingScreenPreview (

) {
    SearchingScreen()
}