package es.josevaldes.data.model

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
        get() = "$contentType-${genres?.joinToString("|") { it.id.toString() }}-${
            providers?.joinToString(
                "|"
            ) { it.id.toString() }
        }-${duration?.duration}-${score?.score}-${yearFrom}-${yearTo}-${sortBy}".md5()
}