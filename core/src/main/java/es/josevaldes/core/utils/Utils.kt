package es.josevaldes.core.utils

import java.util.Locale

fun getDeviceLocale(): String {
    val locale: Locale = Locale.getDefault()
    return "${locale.language}-${locale.country}"
}

