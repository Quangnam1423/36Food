package com.example.a36food.presentation.screens.register

import androidx.compose.ui.text.input.TextFieldValue
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a36food.R
import com.example.a36food.presentation.viewmodel.RegisterState
import com.example.a36food.presentation.viewmodel.RegisterViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNetworkError: () -> Unit = {}
) {
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Set up network error callback
    LaunchedEffect(Unit) {
        viewModel.setNetworkErrorCallback(onNetworkError)
    }

    // Navigate to login screen when registration is successful
    LaunchedEffect(registerState.isSuccess) {
        if (registerState.isSuccess) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đăng ký tài khoản") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )

                RegisterForm(
                    registerState = registerState,
                    viewModel = viewModel,
                    focusManager = focusManager
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.register() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5722)
                    ),
                    enabled = !registerState.isLoading && viewModel.isFormValid()
                ) {
                    if (registerState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Đăng ký")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Đã có tài khoản? ")
                    Text(
                        text = "Đăng nhập",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = onLoginClick)
                    )
                }
            }

            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { /* Prevent dismiss on outside click */ },
                    title = { Text("Đăng ký thành công") },
                    text = {
                        Column {
                            Text("Tài khoản của bạn đã được tạo thành công.")
                            Spacer(modifier = Modifier.height(8.dp))

                            if (registerState.isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                    Text("Đang đăng nhập...")
                                }
                            } else {
                                val tokenSaved = viewModel.isUserLoggedIn()
                                if (tokenSaved) {
                                    Text(
                                        text = "Bạn đã được đăng nhập tự động.",
                                        color = Color.Green
                                    )
                                } else {
                                    Text(
                                        text = "Không thể đăng nhập tự động, vui lòng đăng nhập thủ công.",
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                viewModel.clearRegistrationState()
                                onNavigateToHome()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF5722)
                            ),
                            enabled = !registerState.isLoading
                        ) {
                            Text("Tiếp tục")
                        }
                    }
                )
            }

            registerState.errorMessage?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(error)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterForm(
    registerState: RegisterState,
    viewModel: RegisterViewModel,
    focusManager: androidx.compose.ui.focus.FocusManager
) {

    var dobTextFieldValue by remember {
        mutableStateOf(TextFieldValue(registerState.dob))
    }

    LaunchedEffect(registerState.dob) {
        if (dobTextFieldValue.text != registerState.dob) {
            // Only update if typing hasn't changed the field
            dobTextFieldValue = TextFieldValue(
                text = registerState.dob,
                selection = TextRange(registerState.dob.length)
            )
        }
    }
    // Name field
    OutlinedTextField(
        value = registerState.name,
        onValueChange = { viewModel.updateName(it) },
        label = { Text("Họ tên") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true,
        isError = registerState.nameError != null,
        supportingText = registerState.nameError?.let { { Text(it) } }
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Email field
    OutlinedTextField(
        value = registerState.email,
        onValueChange = { viewModel.updateEmail(it) },
        label = { Text("Email") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true,
        isError = registerState.emailError != null,
        supportingText = registerState.emailError?.let { { Text(it) } }
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Phone field
    OutlinedTextField(
        value = registerState.phone,
        onValueChange = { viewModel.updatePhone(it) },
        label = { Text("Số điện thoại") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true,
        isError = registerState.phoneError != null,
        supportingText = registerState.phoneError?.let { { Text(it) } }
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Gender dropdown
    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Nam", "Nữ", "Khác")

    ExposedDropdownMenuBox(
        expanded = genderExpanded,
        onExpandedChange = { genderExpanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = registerState.gender,
            onValueChange = {},
            readOnly = true,
            label = { Text("Giới tính") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Gender") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = genderExpanded,
            onDismissRequest = { genderExpanded = false }
        ) {
            genderOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        viewModel.updateGender(option)
                        genderExpanded = false
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // date
    OutlinedTextField(
        value = dobTextFieldValue,
        onValueChange = { newValue ->
            // Remember current cursor position before formatting
            val cursorPosition = newValue.selection.start

            // Update the ViewModel with new text
            viewModel.updateDob(newValue.text)

            // Calculate new cursor position after formatting
            val formatted = registerState.dob
            val addedCharacters = formatted.length - newValue.text.length
            val newCursorPos = if (addedCharacters > 0) {
                // If characters were added (like slashes), move cursor forward
                cursorPosition + addedCharacters
            } else {
                // If deleting or unchanged length, keep cursor at same position
                minOf(formatted.length, cursorPosition)
            }

            // Update TextField with new cursor position
            dobTextFieldValue = TextFieldValue(
                text = formatted,
                selection = TextRange(newCursorPos)
            )
        },
        label = { Text("Ngày sinh") },
        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Date of birth") },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("DD/MM/YYYY") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true,
        isError = registerState.dobError != null,
        supportingText = {
            if (registerState.dobError != null) {
                Text(registerState.dobError)
            } else {
                Text("Nhập 8 chữ số cho ngày sinh (VD: 04122003)")
            }
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Password field
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = registerState.password,
        onValueChange = { viewModel.updatePassword(it) },
        label = { Text("Mật khẩu") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true,
        isError = registerState.passwordError != null,
        supportingText = registerState.passwordError?.let { { Text(it) } }
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Confirm Password field
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = registerState.confirmPassword,
        onValueChange = { viewModel.updateConfirmPassword(it) },
        label = { Text("Xác nhận mật khẩu") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm password") },
        trailingIcon = {
            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                Icon(
                    imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                )
            }
        },
        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        singleLine = true,
        isError = registerState.confirmPasswordError != null,
        supportingText = registerState.confirmPasswordError?.let { { Text(it) } }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Terms and conditions text
    Text(
        text = "Bằng cách đăng ký, bạn đồng ý với các Điều khoản và Điều kiện của chúng tôi",
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onBackClick = {},
        onLoginClick = {}
    )
}