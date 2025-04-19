package com.example.a36food.ui.components


import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Badge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MessageIcon(
    messageCount: Int,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        if (messageCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                    ) {
                        Text(messageCount.toString())
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Message"
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = "Message"
            )
        }
    }
}

@Preview
@Composable
fun MessageIconPreview(

) {
    MessageIcon(
        messageCount = 3,
        onClick = {}
    )
}