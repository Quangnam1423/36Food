package com.example.a36food.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a36food.R
import com.example.a36food.presentation.viewmodel.LoginUiState
import com.example.a36food.presentation.viewmodel.LoginViewModel
import com.example.a36food.ui.theme._36FoodTheme

@Composable
fun LoginScreen(
    loginUiState: LoginUiState,
    modifier: Modifier = Modifier,
    retryAction: () -> Unit,
    loginSuccessAction: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
    ) {

    when (loginUiState) {
        is LoginUiState.Idle -> IdleScreen()
        is LoginUiState.Loading -> LoadingScreen()
        is LoginUiState.Error -> ErrorScreen(retryAction)
        is LoginUiState.Success -> loginSuccessAction
    }
}

@Composable
fun IdleScreen(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // logo
        Image(
            modifier = Modifier
                .fillMaxHeight(),
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.app_name)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = Modifier.fillMaxSize(),
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))


    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {

}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {

}

@Preview(showBackground = true)
@Composable
fun IdleScreenPreview() {
    _36FoodTheme {
        IdleScreen()
    }
}