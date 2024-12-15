package es.josevaldes.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class MovieType {
    MOVIE,
    TVSHOW
}


@Parcelize
data class DiscoverMoviesData(
    val results: List<DiscoverItemData>,
    val page: Int,
    val totalResults: Int,
    val totalPages: Int
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
}


@Parcelize
data class DiscoverMovieData(
    val originalTitle: String? = null,
    val title: String? = null,
    val releaseDate: String? = null,
    val video: Boolean? = null,
    override val id: Int = 0,
    override val adult: Boolean? = null,
    override val backdropPath: String? = null,
    override val genreIds: List<Int> = emptyList(),
    override val originalLanguage: String? = null,
    override val posterPath: String? = null,
    override val overview: String? = null,
    override val voteAverage: Double? = null,
    override val voteCount: Int? = null,
    override val popularity: Double? = null
) : DiscoverItemData()


@Parcelize
data class DiscoverTvData(
    val originalName: String? = null,
    val name: String? = null,
    val firstAirDate: String? = null,
    val originCountry: List<String>? = emptyList(),
    override val id: Int = 0,
    override val adult: Boolean? = null,
    override val backdropPath: String? = null,
    override val genreIds: List<Int> = emptyList(),
    override val originalLanguage: String? = null,
    override val posterPath: String? = null,
    override val overview: String? = null,
    override val voteAverage: Double? = null,
    override val voteCount: Int? = null,
    override val popularity: Double? = null
) : DiscoverItemData()