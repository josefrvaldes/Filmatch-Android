package es.josevaldes.data.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import es.josevaldes.core.utils.joinWithSeparatorAndFinalSeparator
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class Movie(
    @JsonProperty("id") val id: Int,
    @JsonProperty("adult") val adult: Boolean? = null,
    @JsonProperty("backdrop_path") val backdropPath: String? = null,
    @JsonProperty("belongs_to_collection") val belongsToCollection: Collection? = null,
    @JsonProperty("budget") val budget: Int? = null,
    @JsonProperty("genres") val genres: List<Genre> = emptyList(),
    @JsonProperty("homepage") val homepage: String? = null,
    @JsonProperty("imdb_id") val imdbId: String? = null,
    @JsonProperty("origin_country") val originCountry: List<String>? = emptyList(),
    @JsonProperty("original_language") val originalLanguage: String? = null,
    @JsonProperty("original_title") val originalTitle: String? = null,
    @JsonProperty("overview") val overview: String? = null,
    @JsonProperty("popularity") val popularity: Double? = null,
    @JsonProperty("poster_path") val posterPath: String? = null,
    @JsonProperty("production_companies") val productionCompanies: List<ProductionCompany>? = emptyList(),
    @JsonProperty("production_countries") val productionCountries: List<ProductionCountry>? = emptyList(),
    @JsonProperty("release_date") val releaseDate: String? = null,
    @JsonProperty("revenue") val revenue: Long? = null,
    @JsonProperty("runtime") val runtime: Int? = null,
    @JsonProperty("spoken_languages") val spokenLanguages: List<SpokenLanguage>? = emptyList(),
    @JsonProperty("status") val status: String? = null,
    @JsonProperty("tagline") val tagline: String? = null,
    @JsonProperty("title") val title: String? = null,
    @JsonProperty("video") val video: Boolean? = null,
    @JsonProperty("vote_average") val voteAverage: Double? = null,
    @JsonProperty("vote_count") val voteCount: Int? = null,
    @JsonProperty("credits") val credits: Credits? = null,
    @JsonProperty("videos") val videos: Videos? = null
) : Parcelable {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500${posterPath}"

    val displayableYoutubeVideos: List<VideoResult>
        get() = videos?.results?.filter { it.site == "YouTube" && it.key?.isNotEmpty() == true }
            ?.reversed() // for some reason, the API returns the videos in what I consider the reversed order, like, the trailers are the last videos, and the featurettes are the first ones
            ?: emptyList()

    val displayableCast: List<CastMember>
        get() = credits?.cast?.filter { it.profilePath?.isNotEmpty() == true } ?: emptyList()

    fun getGenresString(andSeparator: String = "and"): String {
        return joinWithSeparatorAndFinalSeparator(
            finalSeparator = " $andSeparator ",
            list = genres.map { it.name }
        )
    }

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
@JsonIgnoreProperties(ignoreUnknown = true)
data class Collection(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("poster_path") val posterPath: String? = null,
    @JsonProperty("backdrop_path") val backdropPath: String? = null
) : Parcelable

@Serializable
@Parcelize
data class Genre(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String
) : Parcelable

@Serializable
@Parcelize
data class ProductionCompany(
    @JsonProperty("id") val id: Int,
    @JsonProperty("logo_path") val logoPath: String? = null,
    @JsonProperty("name") val name: String,
    @JsonProperty("origin_country") val originCountry: String
) : Parcelable

@Serializable
@Parcelize
data class ProductionCountry(
    @JsonProperty("iso_3166_1") val isoCode: String,
    @JsonProperty("name") val name: String
) : Parcelable

@Serializable
@Parcelize
data class SpokenLanguage(
    @JsonProperty("english_name") val englishName: String,
    @JsonProperty("iso_639_1") val isoCode: String,
    @JsonProperty("name") val name: String
) : Parcelable


@Serializable
@Parcelize
data class Credits(
    @JsonProperty("cast") val cast: List<CastMember> = emptyList(),
    @JsonProperty("crew") val crew: List<CrewMember> = emptyList()
) : Parcelable

@Serializable
@Parcelize
data class CastMember(
    @JsonProperty("adult") val adult: Boolean,
    @JsonProperty("gender") val gender: Int?,
    @JsonProperty("id") val id: Int,
    @JsonProperty("known_for_department") val knownForDepartment: String?,
    @JsonProperty("name") val name: String,
    @JsonProperty("original_name") val originalName: String?,
    @JsonProperty("popularity") val popularity: Float?,
    @JsonProperty("profile_path") val profilePath: String?,
    @JsonProperty("cast_id") val castId: Int?,
    @JsonProperty("character") val character: String?,
    @JsonProperty("credit_id") val creditId: String?,
    @JsonProperty("order") val order: Int
) : Parcelable {
    val profileUrl: String
        get() = "https://image.tmdb.org/t/p/w185${profilePath}"
}

@Serializable
@Parcelize
data class CrewMember(
    @JsonProperty("adult") val adult: Boolean? = null,
    @JsonProperty("gender") val gender: Int? = null,
    @JsonProperty("id") val id: Int,
    @JsonProperty("known_for_department") val knownForDepartment: String? = null,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("original_name") val originalName: String? = null,
    @JsonProperty("popularity") val popularity: Float? = null,
    @JsonProperty("profile_path") val profilePath: String? = null,
    @JsonProperty("cast_id") val creditId: String? = null,
    @JsonProperty("department") val department: String? = null,
    @JsonProperty("job") val job: String? = null
) : Parcelable

@Serializable
@Parcelize
data class Videos(
    @JsonProperty("results") val results: List<VideoResult> = emptyList()
) : Parcelable

@Serializable
@Parcelize
data class VideoResult(
    @JsonProperty("iso_639_1") val iso6391: String? = null,
    @JsonProperty("iso_3166_1") val iso31661: String? = null,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("key") val key: String? = null,
    @JsonProperty("site") val site: String? = null,
    @JsonProperty("size") val size: Int? = null,
    @JsonProperty("type") private val type: String? = null,
    @JsonProperty("official") val official: Boolean? = null,
    @JsonProperty("published_at") val publishedAt: String? = null,
    @JsonProperty("id") val id: String? = null
) : Parcelable {
    val videoType: VideoType
        get() = VideoType.from(type)
}

@Serializable
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