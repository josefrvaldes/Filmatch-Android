package es.josevaldes.filmatch.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.ui.theme.getDefaultAccentButtonColors

@Composable
fun SimpleInfoDismissibleDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = body)
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

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SimpleInfoDismissibleDialogPreviewDark() {
    FilmatchTheme(darkTheme = true) {
        SimpleInfoDismissibleDialog(
            title = "Title",
            body = "Body",
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleInfoDismissibleDialogPreviewLight() {
    FilmatchTheme(darkTheme = false) {
        SimpleInfoDismissibleDialog(
            title = "Title",
            body = "Body",
            onDismiss = {}
        )
    }
}