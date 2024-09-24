package es.josevaldes.filmatch.utils

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import java.util.Locale

fun getDeviceLocale(): String {
    val locale: Locale = Locale.getDefault()
    return "${locale.language}-${locale.country}"
}

