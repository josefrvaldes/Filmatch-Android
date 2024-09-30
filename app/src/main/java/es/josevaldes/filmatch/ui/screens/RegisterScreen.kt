package es.josevaldes.filmatch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.josevaldes.core.utils.validateEmail
import es.josevaldes.core.utils.validatePassword
import es.josevaldes.data.results.AuthResult
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.errors.ErrorMessageWrapper
import es.josevaldes.filmatch.navigation.Screen
import es.josevaldes.filmatch.ui.components.EmailTextField
import es.josevaldes.filmatch.ui.components.PasswordTextField
import es.josevaldes.filmatch.ui.dialogs.ErrorDialog
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController) {
    val authViewModel: AuthViewModel = hiltViewModel()

    val email = remember { mutableStateOf("") }
    val pass1 = remember { mutableStateOf("") }
    val pass2 = remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val registerResult = authViewModel.authResult.collectAsState()


    fun isValidForm(): Boolean {
        return validateEmail(email.value) && validatePassword(pass1.value) && pass1.value == pass2.value
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
            PasswordTextField(
                pass2,
                label = stringResource(R.string.repeat_password),
                imeAction = ImeAction.Done,
                isError = pass1.value != pass2.value,
                supportingText = stringResource(R.string.passwords_don_t_match_error_message),
            )

            Button(
                onClick = {
                    if (isValidForm()) {
                        authViewModel.register(email.value, pass1.value)
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


    when (val result = registerResult.value) {
        is AuthResult.Success -> showSuccessDialog = true
        is AuthResult.Error -> {
            errorMessage =
                ErrorMessageWrapper(LocalContext.current).getErrorMessage(result.authError)
            authViewModel.clearError()
        }

        null -> {} // do nothing
    }


    if (showSuccessDialog) {
        SuccessDialog {
            showSuccessDialog = false
            navController.navigate(Screen.LoginScreen.route) {
                // this will clean the stuck up to AuthScreen except for AuthScreen itself
                popUpTo(Screen.AuthScreen.route) { inclusive = false }
                launchSingleTop = true // avoid multiple instances of login screen
            }
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