package es.josevaldes.filmatch.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.josevaldes.filmatch.R

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    SimpleInfoDismissibleDialog(
        title = stringResource(R.string.error),
        body = message,
    ) { onDismiss() }
}