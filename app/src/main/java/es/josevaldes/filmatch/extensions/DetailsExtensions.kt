package es.josevaldes.filmatch.extensions

import android.content.Context
import es.josevaldes.data.model.DetailsItemData
import es.josevaldes.data.model.DetailsMovieData
import es.josevaldes.data.model.DetailsTvData
import es.josevaldes.filmatch.R

fun DetailsItemData.durationString(context: Context): String {
    val displayableRuntime = this.displayableRuntime()
    return when (this) {
        is DetailsMovieData -> {
            val hours = displayableRuntime / 60
            val minutes = displayableRuntime % 60
            context.getString(R.string.duration_string_movie, hours, minutes)
        }

        is DetailsTvData -> {
            context.getString(R.string.duration_string_tv_show, displayableRuntime)
        }
    }
}