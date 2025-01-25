package es.josevaldes.filmatch.model

import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.InterestStatus

data class SwipeableMovie(
    val movie: DiscoverItemData,
    var isLiked: Boolean? = null,
    var rotation: Float? = null,
    var translationX: Float? = null,
    var translationY: Float? = null,
    var swipedStatus: InterestStatus = InterestStatus.NONE
)