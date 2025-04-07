package com.example.a36food.presentation.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a36food.R
import com.example.a36food.ui.theme._36FoodTheme


@Composable
fun UnconnectedScreen(
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
                .height(screenHeight * 0.04f),
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.app_name)
        )

        Spacer(modifier = Modifier.height(screenHeight * 0.05f))

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding()
                .height(screenHeight * 0.3f),
            painter = painterResource(R.drawable.unconnected),
            contentDescription = "unconnected"
        )

        Spacer(modifier = Modifier.height(screenHeight * 0.01f))

        Text(
            text = "Mất Kết Nối Mạng, Kiểm Tra Kết Nối Và Thử Lại Nhé!",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            color = Color.Yellow,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(screenHeight * 0.04f))

        // Button
        Button(
            onClick = {/*TO DO*/},
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp).
            padding(screenWidth * 0.01f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "THỬ LẠI", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UnconnectedScreenPreview() {
    _36FoodTheme {
        UnconnectedScreen()
    }
}