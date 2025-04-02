package com.example.a36food.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.a36food.presentation.viewmodel.RegisterViewModel
import com.example.a36food.ui.theme._36FoodTheme

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = viewModel(),
    modifier: Modifier = Modifier
    ) {
    val registerUiState by registerViewModel.registerState.collectAsState()

    val passwordVisible by remember {mutableStateOf(false)}
    val confirmPasswordVisible by remember {mutableStateOf(false)}

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
            contentDescription  = stringResource(R.string.app_name)
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_maximum)))

        Text (
            text = "Đăng Ký",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        )

        Registerlayout(
            username = registerUiState.username,
            email = registerUiState.email,
            numberphone = registerUiState.phoneNumber,
            password = registerUiState.password,
            confirmPassword = registerUiState.confirmPassword,
            onUsernameChanged = {},
            onPasswordChanged = {},
            onNumberphoneChanegd = {},
            onEmailChanged = {},
            onConfirmPasswordChanged = {}
        )
    }
}

@Composable
fun Registerlayout(
    username: String,
    email: String,
    numberphone: String,
    password: String,
    confirmPassword: String,
    onUsernameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onNumberphoneChanegd: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) ->Unit
) {

    var passwordVisible by remember{mutableStateOf(false)}
    var confirmPasswordVisible by remember{mutableStateOf(false)}

    Card (
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

            Text (
                text = "Tên Đăng Nhập",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            // username field
            OutlinedTextField(
                value = username,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = {onUsernameChanged},
                label = {Text("username")},
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {/*TO DO*/}
                )
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            Text (
                text = "Email",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = email,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = {onEmailChanged},
                label = {Text("example@gmail.com")},
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {/*TO DO*/}
                )
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            Text (
                text = "Số Điện Thoại",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Start)
            )
            OutlinedTextField(
                value = numberphone,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = {onNumberphoneChanegd},
                label = {Text("phone number")},
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {/*TO DO*/}
                )
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            Text (
                text = "Mật Khẩu",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = password,
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
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            Text (
                text = "Nhập Lại Mật Khẩu",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Start)
            )
            OutlinedTextField(
                value = confirmPassword,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = {onConfirmPasswordChanged},
                label = {Text("confirm password")},
                visualTransformation = if (confirmPasswordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(
                        onClick = {confirmPasswordVisible = !confirmPasswordVisible}
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
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

            Button(
                onClick = {/*TO DO*/},
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "ĐĂNG KÝ", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    _36FoodTheme {
        RegisterScreen()
    }
}