package es.josevaldes.filmatch.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import es.josevaldes.data.results.AuthResult
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.errors.ErrorMessageWrapper
import es.josevaldes.filmatch.ui.components.EmailTextField
import es.josevaldes.filmatch.viewmodels.AuthViewModel

@Composable
fun ForgotPasswordDialog(onSuccess: () -> Unit, onDismiss: () -> Unit) {
    val viewModel: AuthViewModel = hiltViewModel()
    val email = remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val forgetPasswordResult = viewModel.forgotPasswordResult.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.forgot_you_password_dialog_title))
        },
        text = {
            Column {
                Text(text = stringResource(R.string.forgot_your_password_dialog_text))
                EmailTextField(email)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.callForgotPassword(email.value)
                }
            ) {
                Text(text = stringResource(R.string.ok))
            }
        }
    )

    when (val result = forgetPasswordResult.value) {
        is AuthResult.Success -> onSuccess()
        is AuthResult.Error -> errorMessage =
            ErrorMessageWrapper(LocalContext.current).getErrorMessage(result.authError)

        null -> {} // do nothing
    }

    if (errorMessage.isNotEmpty()) {
        ErrorDialog(errorMessage) {
            errorMessage = ""
        }
    }
}