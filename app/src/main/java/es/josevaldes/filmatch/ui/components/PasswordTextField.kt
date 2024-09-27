package es.josevaldes.filmatch.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import es.josevaldes.core.utils.validatePassword
import es.josevaldes.filmatch.R

@Composable
fun PasswordTextField(pass: MutableState<String>) {
    OutlinedTextField(
        value = pass.value,
        maxLines = 1,
        onValueChange = { pass.value = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        label = { Text(stringResource(R.string.password)) },
        modifier = Modifier.padding(20.dp),
        visualTransformation = PasswordVisualTransformation(),
        isError = !validatePassword(pass.value),
        supportingText = {
            if (!validatePassword(pass.value)) {
                Text(stringResource(R.string.wrong_password_created_error_message))
            }
        }
    )
}