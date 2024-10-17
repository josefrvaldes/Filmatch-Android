package es.josevaldes.filmatch.ui.dialogs

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import es.josevaldes.filmatch.R

@Composable
fun ErrorDialog(
    message: String,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    onDismiss: () -> Unit
) {
    SimpleInfoDismissibleDialog(
        backgroundColor = backgroundColor,
        title = stringResource(R.string.error),
        body = message,
    ) { onDismiss() }
}