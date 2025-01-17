package es.josevaldes.filmatch.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import es.josevaldes.data.model.VideoResultData
import timber.log.Timber

fun VideoResultData.openYoutubeVideo(context: Context) {
    val youtubeUrl = "https://www.youtube.com/watch?v=$key"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Timber.d("Error opening youtube video: $e")
    }
}