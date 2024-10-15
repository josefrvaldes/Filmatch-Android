package es.josevaldes.filmatch.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import es.josevaldes.core.utils.validatePassword
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.ui.theme.FilmatchTheme

@Composable
fun PasswordTextField(
    pass: MutableState<String>,
    label: String = stringResource(R.string.password),
    imeAction: ImeAction = ImeAction.Next,
    isError: Boolean = !validatePassword(pass.value),
    supportingText: String = stringResource(R.string.wrong_password_created_error_message),
    isEnabled: Boolean = true,
    shouldDisplayErrors: Boolean = false,
) {
    OutlinedTextField(
        enabled = isEnabled,
        value = pass.value,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
        onValueChange = { pass.value = it },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        label = { Text(label) },
        visualTransformation = PasswordVisualTransformation(),
        isError = shouldDisplayErrors && isError,
        supportingText = {
            if (shouldDisplayErrors && isError) {
                Text(supportingText)
            }
        }
    )
}


@Preview
@Composable
fun PasswordTextFieldPreview() {
    val email = remember { mutableStateOf("") }
    FilmatchTheme {
        PasswordTextField(pass = email)
    }
}

@Preview
@Composable
fun PasswordTextFieldFilledPreview() {
    val email = remember { mutableStateOf("jose@gmail.com") }
    FilmatchTheme {
        PasswordTextField(pass = email)
    }
}