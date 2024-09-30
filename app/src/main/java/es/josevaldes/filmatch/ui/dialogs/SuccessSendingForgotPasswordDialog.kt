package es.josevaldes.filmatch.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.josevaldes.filmatch.R

@Composable
fun SuccessSendingForgotPasswordDialog(onDismiss: () -> Unit) {
    SimpleInfoDismissibleDialog(
        title = stringResource(R.string.success),
        body = stringResource(R.string.success_sending_forgot_password_dialog_body),
        onDismiss = onDismiss
    )
}