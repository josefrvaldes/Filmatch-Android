package es.josevaldes.filmatch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.navigation.Screen
import es.josevaldes.filmatch.ui.dialogs.ErrorDialog
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController) {

    val authViewModel: AuthViewModel = hiltViewModel()

    var email by remember { mutableStateOf("") }
    var pass1 by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }


    fun isValidForm(): Boolean {
        return !isEmailError(email) && validatePassword(pass1) && pass1 == pass2
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .fillMaxHeight()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            OutlinedTextField(
                value = email,
                maxLines = 1,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.padding(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = isEmailError(email),
                supportingText = {
                    if (email.isEmpty()) {
                        Text(stringResource(R.string.enter_your_email))
                    } else if (isEmailError(email)) {
                        Text(stringResource(R.string.invalid_email))
                    }
                }
            )
            OutlinedTextField(
                value = pass1,
                maxLines = 1,
                onValueChange = { pass1 = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.padding(20.dp),
                visualTransformation = PasswordVisualTransformation(),
                isError = !validatePassword(pass1),
                supportingText = {
                    if (!validatePassword(pass1)) {
                        Text(stringResource(R.string.wrong_password_created_error_message))
                    }
                }
            )
            OutlinedTextField(
                maxLines = 1,
                value = pass2,
                onValueChange = { pass2 = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                label = { Text(stringResource(R.string.repeat_password)) },
                modifier = Modifier.padding(20.dp),
                visualTransformation = PasswordVisualTransformation(),
                isError = pass1 != pass2,
                supportingText = {
                    if (pass1 != pass2) {
                        Text(stringResource(R.string.passwords_don_t_match_error_message))
                    }
                }
            )

            Button(
                onClick = {
                    if (isValidForm()) {
                        authViewModel.register(email, pass1, { _ ->
                            showSuccessDialog = true
                        }, { error ->
                            errorMessage = error
                        })
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            ) {
                Text(stringResource(R.string.register))
            }
        }
    }


    if (showSuccessDialog) {
        SuccessDialog {
            showSuccessDialog = false
            navController.navigate(Screen.LoginScreen.route)
        }
    } else if (errorMessage.isNotEmpty()) {
        ErrorDialog(errorMessage) { errorMessage = "" }
    }
}

@Composable
private fun SuccessDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        title = { Text(text = stringResource(R.string.user_registered_successfully_dialog_title)) },
        text = { Text(text = stringResource(R.string.user_registered_successfully_dialog_body)) },
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(
                    text = stringResource(R.string.dismiss)
                )
            }
        }
    )
}

private fun isEmailError(email: String): Boolean {
    return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun validatePassword(pass: String): Boolean {
    val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}\$")
    return regex.matches(pass)
}


@Preview
@Composable
fun RegisterScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        RegisterScreen(rememberNavController())
    }
}