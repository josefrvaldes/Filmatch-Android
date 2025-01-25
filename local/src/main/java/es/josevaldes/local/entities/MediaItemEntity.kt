package es.josevaldes.local.entities

import androidx.room.Entity

enum class MediaEntityType {
    MOVIE,
    TV
}

enum class InterestStatus {
    INTERESTED,
    SUPER_INTERESTED,
    NOT_INTERESTED,
    WATCHED,
    NONE
}

@Entity(
    tableName = "media_items",
    primaryKeys = ["id", "type"]
)
data class MediaItemEntity(
    val id: Int,
    val type: MediaEntityType,
    val originalTitle: String? = null,
    val title: String? = null,
    val releaseDate: String? = null,
    val video: Boolean? = null,
    val originalName: String? = null,
    val name: String? = null,
    val firstAirDate: String? = null,
    val originCountry: String? = null,
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: String? = null,
    val originalLanguage: String? = null,
    val posterPath: String? = null,
    val overview: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val popularity: Double? = null
)