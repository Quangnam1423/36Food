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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a36food.R
import com.example.a36food.presentation.viewmodel.ForgotPasswordViewModel
import com.example.a36food.ui.theme._36FoodTheme

@Composable
fun ChangePassScreen(
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val email: String? = null

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
            text = stringResource(R.string.change_password),
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
                    value = viewModel.email,
                    singleLine = true,
                    shape = shapes.large,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface,
                        disabledContainerColor = colorScheme.surface,
                    ),
                    onValueChange = {/*TO DO*/},
                    label = {Text(text = "example@gmail.com")},
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {/*TO DO*/}
                    )
                )

                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))

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
    }
}

@Composable
fun EnterEmailScreen() {

}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    _36FoodTheme {
        ChangePassScreen()
    }
}