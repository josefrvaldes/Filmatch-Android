package es.josevaldes.core.utils

import android.util.Base64
import java.security.SecureRandom
import java.util.Locale


/**
 * Retrieves the device's current locale in the format "language-country".
 *
 * Example:
 * - For a device set to Spanish in Spain, it will return "es-ES".
 * - For a device set to English in the United States, it will return "en-US".
 *
 * @return A string representing the device's locale in the format "language-country".
 */
fun getDeviceLocale(): String {
    val locale: Locale = Locale.getDefault()
    return "${locale.language}-${locale.country}"
}

/**
 * Retrieves the device's current country code in ISO 3166-1 alpha-2 format.
 *
 * Example:
 * - For a device set to Spain, it will return "ES".
 * - For a device set to the United States, it will return "US".
 *
 * @return A string representing the device's country code in uppercase.
 */
fun getDeviceCountry(): String {
    return Locale.getDefault().country
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

fun joinWithSeparatorAndFinalSeparator(
    separator: String = ", ",
    finalSeparator: String,
    list: List<String>
): String {
    var categoriesString = ""
    for (i in list.size - 1 downTo 0) {
        when (i) {
            list.size - 1 -> {
                categoriesString += list[i]
            }

            list.size - 2 -> {
                categoriesString = "${list[i]}$finalSeparator$categoriesString"
            }

            else -> {
                categoriesString = "${list[i]}$separator$categoriesString"
            }
        }
    }
    return categoriesString
}