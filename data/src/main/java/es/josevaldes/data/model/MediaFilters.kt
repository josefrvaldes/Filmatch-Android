package es.josevaldes.data.model

import androidx.annotation.VisibleForTesting
import es.josevaldes.core.utils.md5
import java.time.LocalDateTime

data class MediaFilters(
    val contentType: ContentType = ContentType.MOVIES,
    val genres: List<GenreData>? = listOf(),
    val providers: List<Provider>? = listOf(),
    val duration: Duration? = null,
    val score: Score? = null,
    val yearFrom: Int? = 2000,
    val yearTo: Int? = LocalDateTime.now().year,
    val sortBy: String = "popularity.desc",
) {
    val filtersHash: String
        get() = getFiltersHashString().md5()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getFiltersHashString(): String {
        val contentTypeString = when (contentType) {
            ContentType.MOVIES -> "movie"
            ContentType.TV_SHOWS -> "tv"
        }
        return "$contentTypeString-${
            genres?.sortedBy { it.id }?.joinToString("|") { it.id.toString() } ?: ""
        }-${
            providers?.sortedBy { it.id }?.joinToString(
                "|"
            ) { it.id.toString() } ?: ""
        }-${duration?.duration}-${score?.score}-${yearFrom}-${yearTo}-${sortBy}"
    }
}