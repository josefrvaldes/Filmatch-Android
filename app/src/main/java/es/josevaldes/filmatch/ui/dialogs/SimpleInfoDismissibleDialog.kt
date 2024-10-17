package es.josevaldes.filmatch.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.ui.theme.getDefaultAccentButtonColors

@Composable
fun SimpleInfoDismissibleDialog(
    title: String,
    body: String,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = if (backgroundColor == MaterialTheme.colorScheme.onSurface) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = body,
                color = if (backgroundColor == MaterialTheme.colorScheme.onSurface) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = getDefaultAccentButtonColors()
            ) {
                Text(text = stringResource(R.string.ok))
            }
        }
    )
}