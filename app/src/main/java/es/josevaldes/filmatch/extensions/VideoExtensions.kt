package es.josevaldes.filmatch.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import es.josevaldes.data.model.VideoResult
import timber.log.Timber

fun VideoResult.openYoutubeVideo(context: Context) {
    val youtubeUrl = "https://www.youtube.com/watch?v=$key"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Timber.d("Error opening youtube video: $e")
    }
}