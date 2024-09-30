package es.josevaldes.core.utils

import android.util.Base64
import java.security.SecureRandom
import java.util.Locale

fun getDeviceLocale(): String {
    val locale: Locale = Locale.getDefault()
    return "${locale.language}-${locale.country}"
}


fun generateNonce(): String {
    val randomBytes = ByteArray(16)
    SecureRandom().nextBytes(randomBytes)
    return Base64.encodeToString(randomBytes, Base64.NO_WRAP or Base64.URL_SAFE)
}

fun validatePassword(pass: String): Boolean {
    val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}\$")
    return regex.matches(pass)
}

fun validateEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}