package es.josevaldes.data.model

data class MovieFilters(
    val contentType: ContentType = ContentType.MOVIES,
    val genres: List<Genre>? = null,
    val providers: List<Provider>? = null,
    val duration: Duration? = null,
    val score: Score? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val sortBy: String = "popularity.desc",
)