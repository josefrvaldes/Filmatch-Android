package es.josevaldes.filmatch.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable

@Composable
fun getWelcomeScreenInputFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary,
    errorBorderColor = MaterialTheme.colorScheme.onSecondary,
    focusedTextColor = MaterialTheme.colorScheme.inverseOnSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.inverseOnSurface,
    disabledTextColor = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.6f),
    errorTextColor = MaterialTheme.colorScheme.error,
    focusedSupportingTextColor = MaterialTheme.colorScheme.inverseOnSurface,
    disabledSupportingTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
    errorSupportingTextColor = MaterialTheme.colorScheme.error,
    unfocusedSupportingTextColor = MaterialTheme.colorScheme.error,
    focusedLabelColor = MaterialTheme.colorScheme.onSecondary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondary,
    disabledBorderColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
    disabledLabelColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f)
)