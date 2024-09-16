package es.josevaldes.filmatch.model

data class Movie(
    val id: Int,
    val title: String,
    val photoUrl: String,
    val description: String? = null,
    val rating: Float? = null,
    val releaseDate: String? = null,
    val genres: List<String> = emptyList()
)

enum class MovieSwipedStatus {
    LIKED,
    DISLIKED,
    NONE
}

data class SwipeableMovie(
    val movie: Movie,
    var isLiked: Boolean? = null,
    var rotation: Float? = null,
    var traslationX: Float? = null,
    var traslationY: Float? = null,
    var swipedStatus: MovieSwipedStatus = MovieSwipedStatus.NONE
)