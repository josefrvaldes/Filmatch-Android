package es.josevaldes.local.entities

import androidx.room.Entity

enum class InterestStatus {
    INTERESTED,
    SUPER_INTERESTED,
    NOT_INTERESTED,
    WATCHED,
}

@Entity(tableName = "movies", primaryKeys = ["id"])
data class Movie(
    val id: Int,
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genresIds: List<Int> = emptyList(),
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val interestStatus: InterestStatus? = null,
)