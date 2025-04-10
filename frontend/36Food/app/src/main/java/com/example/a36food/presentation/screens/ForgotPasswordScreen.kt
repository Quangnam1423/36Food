package com.example.a36food.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a36food.R
import com.example.a36food.presentation.viewmodel.ForgotPasswordUiState
import com.example.a36food.presentation.viewmodel.ForgotPasswordViewModel
import com.example.a36food.ui.theme._36FoodTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ChangePassScreen(
    viewModel: ForgotPasswordViewModel = viewModel(),
    uiState: ForgotPasswordUiState = viewModel.uiState

) {
    val context = LocalContext.current
    val title = when (uiState) {
        is ForgotPasswordUiState.InputEmail -> "Quên Mật Khẩu"
        is ForgotPasswordUiState.VerifyCode -> "Xác Thực"
        is ForgotPasswordUiState.ChangePassWord -> "Nhập Mật Khẩu Mới"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_maximum)))

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.app_name)
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_maximum)))

        Text(
            text = title,
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
            when (uiState) {
                is ForgotPasswordUiState.InputEmail -> {
                    EnterEmailLayout(
                        email = uiState.email,
                        onEmailChanged = viewModel::onEmailChanged
                    )
                }

                is ForgotPasswordUiState.VerifyCode -> {
                    EnterVerificationCodeLayout()
                }

                is ForgotPasswordUiState.ChangePassWord -> {
                    ChangePasswordLayout()
                }
            }
        }
    }
}

@Composable
fun EnterEmailLayout(
    email: String,
    onEmailChanged: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp).fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_maximum)))

        Text(
            text = stringResource(R.string.email),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.Start)
        )


        // password field
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
            label = {Text(text = "example@gmail.com")},
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {/*TO DO*/}
            )
        )

        Spacer(modifier = Modifier.height(screenHeight * 0.2f))

        // Button
        Button(
            onClick = {/*TO DO*/},
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "GỬI MÃ", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EnterVerificationCodeLayout(

) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var otp by remember {mutableStateOf("")}
    var countdown by remember {mutableStateOf(60)}
    var isResendEnabled by remember {mutableStateOf(false)}

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(100L)
            countdown--
        } else {
            isResendEnabled = true
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp).fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(screenHeight * 0.02f))

        Row(

        ) {
            repeat(4) { index ->
                OutlinedTextField(
                    value = otp.getOrNull(index)?.toString() ?: "",
                    onValueChange = { if (it.length <= 1) otp = otp.take(index) + it + otp.drop(index + 1) },
                    modifier = Modifier
                        .size(screenWidth * 0.2f)
                        .padding(screenWidth * 0.015f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface,
                        disabledContainerColor = colorScheme.surface,
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(screenHeight * 0.01f))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = screenWidth * 0.01f)
        ) {
            Text(
                text = "Chưa nhận được mã?"
            )
            Spacer(modifier = Modifier.width(screenWidth * 0.02f))

            Text(
                text = if (!isResendEnabled) "Gửi lại sau ${countdown}s" else "Gửi lại mã",
                color = if (isResendEnabled) Color.Green else Color.Gray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable (enabled = isResendEnabled){
                    if (isResendEnabled) {
                        // resend verify code from modelview
                        isResendEnabled = false
                        countdown = 60
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(screenHeight * 0.2f))

        // Button
        Button(
            onClick = {/*TO DO*/},
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "XÁC NHẬN", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ChangePasswordLayout(

) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val confirmPasswordVisibility = remember { mutableStateOf(false) }
    val newPasswordVisibility = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp).fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_maximum)))

        Text(
            text = "new password",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.Start)
        )


        // password field
        OutlinedTextField(
            value = "",
            singleLine = true,
            shape = shapes.large,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                disabledContainerColor = colorScheme.surface,
            ),
            onValueChange = {/*TO DO*/},
            label = {Text(text = "Mật Khẩu")},
            visualTransformation = if (newPasswordVisibility.value)
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (newPasswordVisibility.value)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(
                    onClick = {newPasswordVisibility.value = !newPasswordVisibility.value}
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

        Spacer(modifier = Modifier.height(height = screenHeight * 0.01f))

        Text(
            text = "confirm password",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        // password field
        OutlinedTextField(
            value = "",
            singleLine = true,
            shape = shapes.large,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                disabledContainerColor = colorScheme.surface,
            ),
            onValueChange = {/*TO DO*/},
            label = {Text(text = "Nhập Lại Mật Khẩu")},
            visualTransformation = if (confirmPasswordVisibility.value)
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisibility.value)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(
                    onClick = {confirmPasswordVisibility.value = !confirmPasswordVisibility.value}
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

        Spacer(modifier = Modifier.height(screenHeight * 0.2f))

        // Button
        Button(
            onClick = {/*TO DO*/},
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "ĐỔI MẬT KHẨU", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    _36FoodTheme {
        ChangePassScreen(
            uiState = ForgotPasswordUiState.InputEmail(
                email = "tongquangnam.official@gmail.com"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EnterVerificationCodeLayoutPreview() {
    _36FoodTheme {
        ChangePassScreen(
            uiState = ForgotPasswordUiState.VerifyCode(
                email = "tongquangnam.official@gmail.com",
                code = "1234",
                newPassword = "asdhasd"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordLayoutPreview() {
    _36FoodTheme {
        ChangePassScreen(
            uiState = ForgotPasswordUiState.ChangePassWord()
        )
    }
}