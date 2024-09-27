package es.josevaldes.filmatch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.josevaldes.core.utils.validateEmail
import es.josevaldes.core.utils.validatePassword
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.navigation.Screen
import es.josevaldes.filmatch.ui.components.EmailTextField
import es.josevaldes.filmatch.ui.components.PasswordTextField
import es.josevaldes.filmatch.ui.dialogs.ErrorDialog
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val authViewModel: AuthViewModel = hiltViewModel()

    val email = remember { mutableStateOf("") }
    val pass1 = remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }


    fun isValidForm(): Boolean {
        return validateEmail(email.value) && validatePassword(pass1.value) && pass1.value == pass2
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .fillMaxHeight()
                .statusBarsPadding()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            EmailTextField(email)
            PasswordTextField(pass1)
            OutlinedTextField(
                maxLines = 1,
                value = pass2,
                onValueChange = { pass2 = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                label = { Text(stringResource(R.string.repeat_password)) },
                modifier = Modifier.padding(20.dp),
                visualTransformation = PasswordVisualTransformation(),
                isError = pass1.value != pass2,
                supportingText = {
                    if (pass1.value != pass2) {
                        Text(stringResource(R.string.passwords_don_t_match_error_message))
                    }
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )

            Button(
                onClick = {
                    if (isValidForm()) {
                        authViewModel.register(email.value, pass1.value, { _ ->
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


@Preview
@Composable
fun RegisterScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        RegisterScreen(rememberNavController())
    }
}