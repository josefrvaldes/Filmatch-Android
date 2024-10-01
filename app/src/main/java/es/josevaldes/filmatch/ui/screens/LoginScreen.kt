package es.josevaldes.filmatch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import es.josevaldes.filmatch.ui.dialogs.ForgotPasswordDialog
import es.josevaldes.filmatch.ui.dialogs.SuccessSendingForgotPasswordDialog
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.AuthViewModel


@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val signInResult = viewModel.authResult.collectAsState()
    var shouldDisplayForgotPasswordDialog by remember { mutableStateOf(false) }
    var shouldDisplaySuccessForgettingPasswordDialog by remember { mutableStateOf(false) }

    fun isValidForm(): Boolean {
        return validateEmail(email.value) && validatePassword(password.value)
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
            verticalArrangement = Arrangement.Center,
        ) {
            EmailTextField(email)
            PasswordTextField(password, imeAction = ImeAction.Done)

            Button(
                onClick = {
                    if (isValidForm()) {
                        viewModel.login(email.value, password.value)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            ) {
                Text(stringResource(R.string.login))
            }
            Text(stringResource(R.string.forgot_your_password), Modifier.clickable {
                shouldDisplayForgotPasswordDialog = true
            })
        }
    }

    when (val result = signInResult.value) {
        is AuthResult.Success -> navController.navigate(Screen.SlideMovieScreen.route) {
            // this will clean the stack up to SlideMovieScreen except for SlideMovieScreen itself
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true // avoid multiple instances of SlideMovieScreen
        }
        is AuthResult.Error -> {
            errorMessage =
                ErrorMessageWrapper(LocalContext.current).getErrorMessage(result.authError)
            viewModel.clearError()
        }

        null -> {} // do nothing
    }

    if (errorMessage.isNotEmpty()) {
        viewModel.clearError()
        ErrorDialog(errorMessage) { errorMessage = "" }
    } else if (shouldDisplayForgotPasswordDialog) {
        ForgotPasswordDialog(
            onSuccess = {
                shouldDisplayForgotPasswordDialog = false
                shouldDisplaySuccessForgettingPasswordDialog = true
            },
            onDismiss = { shouldDisplayForgotPasswordDialog = false }
        )
    } else if (shouldDisplaySuccessForgettingPasswordDialog) {
        SuccessSendingForgotPasswordDialog { shouldDisplaySuccessForgettingPasswordDialog = false }
    }
}


@Preview
@Composable
fun LoginScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        LoginScreen(rememberNavController())
    }
}