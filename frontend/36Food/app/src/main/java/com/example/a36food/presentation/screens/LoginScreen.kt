package com.example.a36food.presentation.screens


import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a36food.R
import com.example.a36food.presentation.viewmodel.LoginUiState
import com.example.a36food.presentation.viewmodel.LoginViewModel
import com.example.a36food.ui.theme._36FoodTheme

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    modifier: Modifier = Modifier,
    retryAction: () -> Unit,
    loginSuccessAction: () -> Unit,
    ) {
    val loginUiState by loginViewModel.loginState.collectAsState()

    when (loginUiState) {
        is LoginUiState.Idle -> IdleScreen(
            onUsernameChanged = {},
            onPasswordChanged = {},
            usernameValue = loginViewModel.username,
            passwordValue = loginViewModel.password
        )
        is LoginUiState.Loading -> {/*TO DO*/}
        is LoginUiState.Error -> ErrorScreen(retryAction)
        is LoginUiState.Success -> loginSuccessAction
    }
}

@Composable
fun IdleScreen(
    passwordValue: String,
    usernameValue: String,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember {mutableStateOf(false)}
    var rememberMe by remember {mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.app_name)
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_maximum)))
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        )
        
        Card(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(5.dp).fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                // Ten dang nhap
                Text (
                    text = stringResource(R.string.username),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.Start)
                )
                // username field
                OutlinedTextField(
                    value = usernameValue,
                    singleLine = true,
                    shape = shapes.large,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface,
                        disabledContainerColor = colorScheme.surface,
                    ),
                    onValueChange = {onUsernameChanged},
                    label = {Text("example@gmail.com")},
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {/*TO DO*/}
                    )
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                // password
                Text(
                    text = stringResource(R.string.password),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )

                // password field
                OutlinedTextField(
                    value = passwordValue,
                    singleLine = true,
                    shape = shapes.large,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface,
                        disabledContainerColor = colorScheme.surface,
                    ),
                    onValueChange = {onPasswordChanged},
                    label = {Text("password")},
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(
                            onClick = {passwordVisible = !passwordVisible}
                        ) {
                            Icon(imageVector = image, contentDescription = "hidden Icon")
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {/*TO DO*/}
                    )
                )

                // checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = {rememberMe = it}
                        )
                        Text(text = "ghi nhớ tôi")
                    }

                    Text(
                        text = "Quên mật khẩu?",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { /*TO DO*/ }
                    )
                }

                // login button
                Button(
                    onClick = {/*TO DO*/},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "ĐĂNG NHẬP", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Không có tài khoản?",
                        color = Color.Black,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )

                    Text(
                        text = "ĐĂNG KÝ",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.padding_small))
                            .clickable { /*TO DO*/ }
                    )
                }

                Text(
                    text ="Hoặc sử dụng",
                    color = Color.Black,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                )
            }
        }
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

@Preview(showBackground = true)
@Composable
fun IdleScreenPreview() {
    _36FoodTheme {
        IdleScreen(
            "",
            "",
            onUsernameChanged = {},
            onPasswordChanged = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    _36FoodTheme {
        LoadingScreen()
    }
}