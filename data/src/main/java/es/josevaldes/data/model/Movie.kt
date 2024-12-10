package es.josevaldes.data.model

import android.os.Parcelable
import es.josevaldes.core.utils.joinWithSeparatorAndFinalSeparator
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

enum class MovieType {
    MOVIE,
    TVSHOW
}


@Serializable
@Parcelize
data class DiscoverMoviesData(
    val results: List<Movie>,
    val page: Int,
    val totalResults: Int,
    val totalPages: Int
) : Parcelable

@Serializable
@Parcelize
data class Movie(
    val id: Int,
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val collection: Collection? = null,
    val budget: Int? = null,
    val genres: List<Genre> = emptyList(),
    val homepage: String? = null,
    val imdbId: String? = null,
    val originCountry: List<String> = emptyList(),
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val productionCompanies: List<ProductionCompany> = emptyList(),
    val productionCountries: List<ProductionCountry> = emptyList(),
    val releaseDate: String? = null,
    val revenue: Long? = null,
    val runtime: Int? = null,
    val spokenLanguages: List<SpokenLanguage> = emptyList(),
    val status: String? = null,
    val tagline: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val credits: Credits? = null,
    val videos: Videos? = null
) : Parcelable {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500${posterPath}"

    val displayableYoutubeVideos: List<VideoResult>
        get() = videos?.results?.filter { it.site == "YouTube" && it.key?.isNotEmpty() == true }
            ?.reversed() // for some reason, the API returns the videos in what I consider the reversed order, like, the trailers are the last videos, and the featurettes are the first ones
            ?: emptyList()

    val displayableCast: List<CastMember>
        get() = credits?.cast?.filter { it.profilePath?.isNotEmpty() == true } ?: emptyList()

    fun getGenresString(andSeparator: String = "and"): String = joinWithSeparatorAndFinalSeparator(
        finalSeparator = " $andSeparator ",
        list = genres.map { it.name }
    )

    fun getDurationString(): String {
        val hours = runtime?.div(60) ?: 0
        val minutes = runtime?.rem(60) ?: 0
        return "${hours}h ${minutes}m"
    }

    fun getReleaseYear(): String? {
        val regex = "\\b(\\d{4})\\b".toRegex()
        val matchResult = regex.find(releaseDate ?: "")
        return matchResult?.value
    }

    fun getDirectorsString(andSeparator: String): String? {
        val directors =
            credits?.crew?.filter { it.department == "Directing" && it.name?.isNotEmpty() == true }
                ?.mapNotNull { it.name }
        return directors?.let {
            joinWithSeparatorAndFinalSeparator(
                finalSeparator = " $andSeparator ",
                list = it
            )
        }
    }
}

@Serializable
@Parcelize
data class Collection(
    val id: Int,
    val name: String? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null
) : Parcelable

@Serializable
@Parcelize
data class Genre(
    val id: Int,
    val name: String
) : Parcelable {
    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        return other is Genre && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

@Serializable
@Parcelize
data class ProductionCompany(
    val id: Int,
    val logoPath: String? = null,
    val name: String,
    val originCountry: String
) : Parcelable

@Serializable
@Parcelize
data class ProductionCountry(
    val isoCode: String,
    val name: String
) : Parcelable

@Serializable
@Parcelize
data class SpokenLanguage(
    val englishName: String,
    val isoCode: String,
    val name: String
) : Parcelable

@Serializable
@Parcelize
data class Credits(
    val cast: List<CastMember> = emptyList(),
    val crew: List<CrewMember> = emptyList()
) : Parcelable

@Serializable
@Parcelize
data class CastMember(
    val adult: Boolean,
    val gender: Int?,
    val id: Int,
    val knownForDepartment: String?,
    val name: String,
    val originalName: String?,
    val popularity: Float?,
    val profilePath: String?,
    val castId: Int?,
    val character: String?,
    val creditId: String?,
    val order: Int
) : Parcelable {
    val profileUrl: String
        get() = "https://image.tmdb.org/t/p/w185${profilePath}"
}

@Serializable
@Parcelize
data class CrewMember(
    val adult: Boolean? = null,
    val gender: Int? = null,
    val id: Int,
    val knownForDepartment: String? = null,
    val name: String? = null,
    val originalName: String? = null,
    val popularity: Float? = null,
    val profilePath: String? = null,
    val department: String? = null,
    val job: String? = null
) : Parcelable

@Serializable
@Parcelize
data class Videos(
    val results: List<VideoResult> = emptyList()
) : Parcelable

@Serializable
@Parcelize
data class VideoResult(
    val iso6391: String? = null,
    val iso31661: String? = null,
    val name: String? = null,
    val key: String? = null,
    val site: String? = null,
    val size: Int? = null,
    private val type: String? = null,
    val official: Boolean? = null,
    val publishedAt: String? = null,
    val id: String? = null
) : Parcelable {
    val videoType: VideoType
        get() = VideoType.from(type)
}

@Parcelize
data class GenresList(
    val genres: List<Genre>
) : Parcelable

enum class VideoType {
    Featurette,
    Clip,
    Teaser,
    Trailer,
    BehindTheScenes,
    Other;

    companion object {
        fun from(value: String?): VideoType {
            return when (value) {
                "Featurette" -> Featurette
                "Clip" -> Clip
                "Teaser" -> Teaser
                "Trailer" -> Trailer
                "Behind the Scenes" -> BehindTheScenes
                else -> Other // if type is unknown, let's return Other
            }
        }
    }
}