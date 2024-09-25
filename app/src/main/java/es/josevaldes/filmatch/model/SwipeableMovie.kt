package es.josevaldes.filmatch.model

import es.josevaldes.data.model.Movie

enum class MovieSwipedStatus {
    LIKED,
    DISLIKED,
    NONE
}

data class SwipeableMovie(
    val movie: Movie,
    var isLiked: Boolean? = null,
    var rotation: Float? = null,
    var translationX: Float? = null,
    var translationY: Float? = null,
    var swipedStatus: MovieSwipedStatus = MovieSwipedStatus.NONE
)