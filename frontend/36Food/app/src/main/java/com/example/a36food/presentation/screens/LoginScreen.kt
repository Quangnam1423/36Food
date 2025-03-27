package com.example.a36food.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.a36food.R
import com.example.a36food.presentation.viewmodel.LoginUiState
import com.example.a36food.presentation.viewmodel.LoginViewModel
import com.example.a36food.ui.theme._36FoodTheme

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    retryAction: () -> Unit,
    loginSuccessAction: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
    ) {
    val loginUiState by loginViewModel.loginState.collectAsState()

    when (loginUiState) {
        is LoginUiState.Idle -> IdleScreen()
        is LoginUiState.Loading -> {/*TO DO*/}
        is LoginUiState.Error -> ErrorScreen(retryAction)
        is LoginUiState.Success -> loginSuccessAction
    }
}

@Composable
fun IdleScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(),
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.app_name)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.titleLarge
        )

        LoginLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        )
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    //navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth(),
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.app_name)
            )
        }


    }
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {

}

@Composable
fun LoginLayout(
    modifier: Modifier = Modifier,
    usernameValue: String,
    passwordValue: String,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(5.dp)
        ) {
            Text(
                text = stringResource(R.string.username),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Black
            )

            OutlinedTextField(
                isError = true,
                value = usernameValue,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = {/*TO DO*/},
                label = "example@gmail.com",
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {/*TO DO*/}
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IdleScreenPreview() {
    _36FoodTheme {
        IdleScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    _36FoodTheme {
        LoadingScreen()
    }
}