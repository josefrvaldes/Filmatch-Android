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
import androidx.compose.ui.unit.dp
import es.josevaldes.core.utils.validateEmail
import es.josevaldes.filmatch.R

@Composable
fun EmailTextField(email: MutableState<String>) {
    OutlinedTextField(
        value = email.value,
        maxLines = 1,
        onValueChange = { email.value = it },
        label = { Text(stringResource(R.string.email)) },
        modifier = Modifier.padding(20.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = !validateEmail(email.value),
        supportingText = {
            if (email.value.isEmpty()) {
                Text(stringResource(R.string.enter_your_email))
            } else if (!validateEmail(email.value)) {
                Text(stringResource(R.string.invalid_email))
            }
        }
    )
}