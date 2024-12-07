package es.josevaldes.filmatch.ui.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable

@Composable
fun getWelcomeScreenInputFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.secondary,
    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
    errorBorderColor = MaterialTheme.colorScheme.secondary,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    errorTextColor = MaterialTheme.colorScheme.onBackground,
    focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    errorSupportingTextColor = MaterialTheme.colorScheme.error,
    unfocusedSupportingTextColor = MaterialTheme.colorScheme.error,
    focusedLabelColor = MaterialTheme.colorScheme.secondary,
    unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
    disabledBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
    disabledLabelColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
)

@Composable
fun getDefaultAccentButtonColors() = ButtonDefaults.buttonColors(
    contentColor = MaterialTheme.colorScheme.onTertiary,
    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
)