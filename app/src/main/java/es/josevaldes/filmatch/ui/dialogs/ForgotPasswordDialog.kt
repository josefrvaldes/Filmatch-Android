package es.josevaldes.filmatch.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import es.josevaldes.core.utils.validateEmail
import es.josevaldes.data.results.AuthResult
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.errors.ErrorMessageWrapper
import es.josevaldes.filmatch.ui.components.EmailTextField
import es.josevaldes.filmatch.ui.theme.getDefaultAccentButtonColors
import es.josevaldes.filmatch.viewmodels.AuthViewModel

@Composable
fun ForgotPasswordDialog(onSuccess: () -> Unit, onDismiss: () -> Unit) {
    val viewModel: AuthViewModel = hiltViewModel()
    val email = remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val forgetPasswordResult = viewModel.forgotPasswordResult.collectAsState(null)
    val isLoadingStatus = viewModel.isLoading.collectAsState(false)
    var shouldDisplayErrors by remember { mutableStateOf(false) }


    fun isValidForm(): Boolean {
        return validateEmail(email.value)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.forgot_you_password_dialog_title)
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.forgot_your_password_dialog_text)
                )
                EmailTextField(
                    email,
                    isEnabled = !isLoadingStatus.value,
                    shouldDisplayErrors = shouldDisplayErrors
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoadingStatus.value,
                colors = getDefaultAccentButtonColors(),
                onClick = {
                    shouldDisplayErrors = true
                    if (isValidForm()) {
                        viewModel.callForgotPassword(email.value)
                    }
                }
            ) {
                Text(text = stringResource(R.string.ok))
                if (isLoadingStatus.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .size(20.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
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
            viewModel.clearError()
        }
    }
}