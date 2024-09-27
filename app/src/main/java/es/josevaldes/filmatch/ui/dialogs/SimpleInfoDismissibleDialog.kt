package es.josevaldes.filmatch.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.josevaldes.filmatch.R

@Composable
fun SimpleInfoDismissibleDialog(title: String, body: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = body)
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