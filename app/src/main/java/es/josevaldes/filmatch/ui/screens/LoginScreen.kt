package es.josevaldes.filmatch.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import es.josevaldes.core.utils.validateEmail
import es.josevaldes.core.utils.validatePassword
import es.josevaldes.data.results.AuthError
import es.josevaldes.data.results.AuthResult
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.errors.ErrorMessageWrapper
import es.josevaldes.filmatch.ui.components.EmailTextField
import es.josevaldes.filmatch.ui.components.PasswordTextField
import es.josevaldes.filmatch.ui.dialogs.ErrorDialog
import es.josevaldes.filmatch.ui.dialogs.ForgotPasswordDialog
import es.josevaldes.filmatch.ui.dialogs.SuccessSendingForgotPasswordDialog
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.ui.theme.getDefaultAccentButtonColors
import es.josevaldes.filmatch.viewmodels.AuthViewModel


private fun isValidForm(email: String, password: String): Boolean {
    return validateEmail(email) && validatePassword(password)
}

@Composable
fun LoginScreen(onGoToSlideMovieScreen: () -> Unit, onGoToRegisterClicked: () -> Unit) {
    val viewModel: AuthViewModel = hiltViewModel()
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val signInResult = viewModel.authResult.collectAsState(null)
    val shouldDisplayForgotPasswordDialog = remember { mutableStateOf(false) }
    var shouldDisplaySuccessForgettingPasswordDialog by remember { mutableStateOf(false) }
    val isLoadingStatus = viewModel.isLoading.collectAsState(false)
    val shouldDisplayErrors = remember { mutableStateOf(false) }


    LoginScreenContent(
        email,
        password,
        isLoadingStatus,
        shouldDisplayErrors,
        shouldDisplayForgotPasswordDialog,
        onLoginClicked = {
            viewModel.login(email.value, password.value)
        },
        onSignInWithGoogleClicked = {
            viewModel.signInWithGoogle(context)
        },
        onGoToRegisterClicked = onGoToRegisterClicked
    )


    when (val result = signInResult.value) {
        is AuthResult.Success -> {
            onGoToSlideMovieScreen()
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
        ErrorDialog(errorMessage) {
            if (errorMessage == ErrorMessageWrapper(context).getErrorMessage(AuthError.NoCredentialsAvailable)) {
                openAccountSettingsScreen(context)
            }
            errorMessage = ""
        }
    } else if (shouldDisplayForgotPasswordDialog.value) {
        ForgotPasswordDialog(
            onSuccess = {
                shouldDisplayForgotPasswordDialog.value = false
                shouldDisplaySuccessForgettingPasswordDialog = true
            },
            onDismiss = { shouldDisplayForgotPasswordDialog.value = false }
        )
    } else if (shouldDisplaySuccessForgettingPasswordDialog) {
        SuccessSendingForgotPasswordDialog(backgroundColor = MaterialTheme.colorScheme.background) {
            shouldDisplaySuccessForgettingPasswordDialog = false
        }
    }
}

@Composable
private fun LoginScreenContent(
    email: MutableState<String>,
    password: MutableState<String>,
    isLoadingStatus: State<Boolean>,
    shouldDisplayErrors: MutableState<Boolean>,
    shouldDisplayForgotPasswordDialog: MutableState<Boolean>,
    onLoginClicked: () -> Unit = {},
    onSignInWithGoogleClicked: () -> Unit = {},
    onGoToRegisterClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            stringResource(R.string.welcome_back),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        EmailTextField(
            email,
            isEnabled = !isLoadingStatus.value,
            shouldDisplayErrors = shouldDisplayErrors.value
        )
        PasswordTextField(
            password,
            imeAction = ImeAction.Done,
            isEnabled = !isLoadingStatus.value,
            shouldDisplayErrors = shouldDisplayErrors.value
        )

        Button(
            enabled = !isLoadingStatus.value,
            onClick = {
                shouldDisplayErrors.value = true
                if (isValidForm(email.value, password.value)) {
                    onLoginClicked()
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 20.dp)
                .fillMaxWidth(),
            colors = getDefaultAccentButtonColors()
        ) {
            Text(stringResource(R.string.login))
            if (isLoadingStatus.value) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .size(20.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }


        Text(
            stringResource(R.string.forgot_your_password),
            Modifier
                .clickable {
                    shouldDisplayErrors.value = true
                    if (!isLoadingStatus.value) {
                        shouldDisplayForgotPasswordDialog.value = true
                    }
                }
                .padding(bottom = 20.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color.Gray)
            )
            Text("Login with", modifier = Modifier.padding(horizontal = 10.dp))
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color.Gray)
            )
        }




        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = stringResource(R.string.sign_in_with_google),
            modifier = Modifier
                .width(80.dp)
                .height(80.dp)
                .clickable {
                    if (!isLoadingStatus.value) {
                        onSignInWithGoogleClicked()
                    }
                }
                .padding(20.dp)
        )



        Row(modifier = Modifier.padding(bottom = 20.dp)) {
            Text(
                text = stringResource(R.string.don_t_have_an_account_yet),
            )
            Text(
                text = stringResource(R.string.register),
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        if (!isLoadingStatus.value) onGoToRegisterClicked()
                    }
                    .padding(start = 10.dp)
            )
        }
    }
}

fun openAccountSettingsScreen(context: Context) {
    val intent = Intent(Settings.ACTION_SYNC_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenContentPreviewDark() {
    FilmatchTheme(darkTheme = true) {
        LoginScreenContent(
            remember { mutableStateOf("email") },
            remember { mutableStateOf("password") },
            remember { mutableStateOf(false) },
            remember { mutableStateOf(false) },
            remember { mutableStateOf(false) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenContentPreviewLight() {
    FilmatchTheme(darkTheme = false) {
        LoginScreenContent(
            remember { mutableStateOf("email") },
            remember { mutableStateOf("password") },
            remember { mutableStateOf(false) },
            remember { mutableStateOf(false) },
            remember { mutableStateOf(false) }
        )
    }
}