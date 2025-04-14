package com.example.a36food.ui.components

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Badge
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BadgedBox
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CartIcon(
    cartCount: Int,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        BadgedBox(
            badge = {
                if (cartCount > 0) {
                    Badge {
                        Text(cartCount.toString())
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart"
            )
        }
    }
}

@Preview
@Composable
fun CartIconPreview() {
    CartIcon(
        cartCount = 3,
        onClick = {}
    )
}