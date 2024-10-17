package es.josevaldes.filmatch.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import es.josevaldes.core.utils.validateEmail
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.ui.theme.getWelcomeScreenInputFieldColors

@Composable
fun EmailTextField(
    email: MutableState<String>,
    imeAction: ImeAction = ImeAction.Next,
    isEnabled: Boolean = true,
    shouldDisplayErrors: Boolean = false
) {
    OutlinedTextField(
        enabled = isEnabled,
        value = email.value,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
        onValueChange = { email.value = it },
        label = { Text(stringResource(R.string.email)) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        ),
        isError = !validateEmail(email.value) && shouldDisplayErrors,
        supportingText = {
            if (shouldDisplayErrors) {
                if (email.value.isEmpty()) {
                    Text(stringResource(R.string.enter_your_email))
                } else if (!validateEmail(email.value)) {
                    Text(stringResource(R.string.invalid_email))
                }
            }
        },
        trailingIcon = {
            if (email.value.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.content_description_clear_email),
                    modifier = Modifier.clickable {
                        email.value = ""
                    },
                    tint = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        },
        colors = getWelcomeScreenInputFieldColors()
    )
}

@Preview
@Composable
fun EmailTextFieldPreview() {
    val email = remember { mutableStateOf("") }
    FilmatchTheme(darkTheme = true) {
        EmailTextField(email = email)
    }
}

@Preview
@Composable
fun EmailTextFieldFilledPreview() {
    val email = remember { mutableStateOf("jose@gmail.com") }
    FilmatchTheme(darkTheme = true) {
        EmailTextField(email = email)
    }
}