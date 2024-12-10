package es.josevaldes.data.model

import es.josevaldes.core.utils.md5

data class MovieFilters(
    val contentType: ContentType = ContentType.MOVIES,
    val genres: List<Genre>? = null,
    val providers: List<Provider>? = null,
    val duration: Duration? = null,
    val score: Score? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val sortBy: String = "popularity.desc",
) {
    val filtersHash: String
        get() = "$contentType-${genres?.joinToString("|") { it.id.toString() }}-${
            providers?.joinToString(
                "|"
            ) { it.id.toString() }
        }-${duration?.duration}-${score?.score}-${yearFrom}-${yearTo}-${sortBy}".md5()
}