package es.josevaldes.data.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

enum class MovieType {
    MOVIE,
    TVSHOW
}


@Parcelize
data class DiscoverMoviesData(
    @JsonProperty("results") val results: List<DiscoverItemData>,
    @JsonProperty("page") val page: Int,
    @JsonProperty("total_results") val totalResults: Int,
    @JsonProperty("total_pages") val totalPages: Int
) : Parcelable


@Parcelize
open class DiscoverItemData(
    open val id: Int = 0,
    open val adult: Boolean? = null,
    open val backdropPath: String? = null,
    open val genreIds: List<Int> = emptyList(),
    open val originalLanguage: String? = null,
    open val posterPath: String? = null,
    open val overview: String? = null,
    open val voteAverage: Double? = null,
    open val voteCount: Int? = null,
    open val popularity: Double? = null
) : Parcelable {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"

    val displayTitle: String?
        get() = when (this) {
            is DiscoverMovieData -> title
            is DiscoverTvData -> name
            else -> null
        }

    val displayOriginalTitle: String?
        get() = when (this) {
            is DiscoverMovieData -> originalTitle
            is DiscoverTvData -> originalName
            else -> null
        }

    val displayReleaseDate: String?
        get() = when (this) {
            is DiscoverMovieData -> releaseDate
            is DiscoverTvData -> firstAirDate
            else -> null
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiscoverItemData

        if (id != other.id) return false
        if (adult != other.adult) return false
        if (backdropPath != other.backdropPath) return false
        if (genreIds != other.genreIds) return false
        if (originalLanguage != other.originalLanguage) return false
        if (posterPath != other.posterPath) return false
        if (overview != other.overview) return false
        if (voteAverage != other.voteAverage) return false
        if (voteCount != other.voteCount) return false
        if (popularity != other.popularity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (adult?.hashCode() ?: 0)
        result = 31 * result + (backdropPath?.hashCode() ?: 0)
        result = 31 * result + genreIds.hashCode()
        result = 31 * result + (originalLanguage?.hashCode() ?: 0)
        result = 31 * result + (posterPath?.hashCode() ?: 0)
        result = 31 * result + (overview?.hashCode() ?: 0)
        result = 31 * result + (voteAverage?.hashCode() ?: 0)
        result = 31 * result + (voteCount ?: 0)
        result = 31 * result + (popularity?.hashCode() ?: 0)
        return result
    }
}


@Parcelize
data class DiscoverMovieData(
    @JsonProperty("original_title") val originalTitle: String? = null,
    @JsonProperty("title") val title: String? = null,
    @JsonProperty("release_date") val releaseDate: String? = null,
    @JsonProperty("video") val video: Boolean? = null,
    @JsonProperty("id") override val id: Int = 0,
    @JsonProperty("adult") override val adult: Boolean? = null,
    @JsonProperty("backdrop_path") override val backdropPath: String? = null,
    @JsonProperty("genre_ids") override val genreIds: List<Int> = emptyList(),
    @JsonProperty("original_language") override val originalLanguage: String? = null,
    @JsonProperty("poster_path") override val posterPath: String? = null,
    @JsonProperty("overview") override val overview: String? = null,
    @JsonProperty("vote_average") override val voteAverage: Double? = null,
    @JsonProperty("vote_count") override val voteCount: Int? = null,
    @JsonProperty("popularity") override val popularity: Double? = null
) : DiscoverItemData()


@Parcelize
data class DiscoverTvData(
    @JsonProperty("original_name") val originalName: String? = null,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("first_air_date") val firstAirDate: String? = null,
    @JsonProperty("origin_country") val originCountry: List<String>? = emptyList(),
    @JsonProperty("id") override val id: Int = 0,
    @JsonProperty("adult") override val adult: Boolean? = null,
    @JsonProperty("backdrop_path") override val backdropPath: String? = null,
    @JsonProperty("genre_ids") override val genreIds: List<Int> = emptyList(),
    @JsonProperty("original_language") override val originalLanguage: String? = null,
    @JsonProperty("poster_path") override val posterPath: String? = null,
    @JsonProperty("overview") override val overview: String? = null,
    @JsonProperty("vote_average") override val voteAverage: Double? = null,
    @JsonProperty("vote_count") override val voteCount: Int? = null,
    @JsonProperty("popularity") override val popularity: Double? = null
) : DiscoverItemData()