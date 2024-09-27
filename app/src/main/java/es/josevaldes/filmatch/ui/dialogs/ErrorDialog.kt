package es.josevaldes.filmatch.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.josevaldes.filmatch.R

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.error))
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.ok))
            }
        }
    )
}