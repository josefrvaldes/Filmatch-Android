package es.josevaldes.data.model

import android.os.Parcelable
import es.josevaldes.core.utils.joinWithSeparatorAndFinalSeparator
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
data class DetailsData(
    val detailsItem: DetailsItemData
) : Parcelable

@Parcelize
@Serializable
sealed class DetailsItemData(
    @SerialName("id") open val id: Int,
    @SerialName("adult") open val adult: Boolean? = null,
    @SerialName("backdrop_path") open val backdropPath: String? = null,
    @SerialName("genres") open val genres: List<GenreData> = emptyList(),
    @SerialName("homepage") open val homepage: String? = null,
    @SerialName("original_language") open val originalLanguage: String? = null,
    @SerialName("overview") open val overview: String? = null,
    @SerialName("popularity") open val popularity: Double? = null,
    @SerialName("poster_path") open val posterPath: String? = null,
    @SerialName("production_companies") open val productionCompanies: List<ProductionCompanyData> = emptyList(),
    @SerialName("production_countries") open val productionCountries: List<ProductionCountryData> = emptyList(),
    @SerialName("spoken_languages") open val spokenLanguages: List<SpokenLanguageData> = emptyList(),
    @SerialName("status") open val status: String? = null,
    @SerialName("tagline") open val tagline: String? = null,
    @SerialName("vote_average") open val voteAverage: Double? = null,
    @SerialName("vote_count") open val voteCount: Int? = null,
    @SerialName("credits") open val credits: CreditsData? = null,
    @SerialName("videos") open val videos: VideosData? = null
) : Parcelable {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500${posterPath}"

    val displayableYoutubeVideos: List<VideoResultData>
        get() = videos?.results?.filter { it.site == "YouTube" && it.key?.isNotEmpty() == true }
            ?.reversed() // for some reason, the API returns the videos in what I consider the reversed order, like, the trailers are the last videos, and the featurettes are the first ones
            ?: emptyList()

    val displayableCast: List<CastMemberData>
        get() = credits?.cast?.filter { it.profilePath?.isNotEmpty() == true } ?: emptyList()

    val displayTitle: String
        get() = when (this) {
            is DetailsMovieData -> title ?: ""
            is DetailsTvData -> name ?: ""
        }


    fun getGenresString(andSeparator: String = "and"): String = joinWithSeparatorAndFinalSeparator(
        finalSeparator = " $andSeparator ",
        list = genres.map { it.name }
    )

    fun displayableRuntime(): Int {
        return when (this) {
            is DetailsMovieData -> runtime ?: 0
            is DetailsTvData -> {
                var runtimeInMinutes = episodeRunTime.firstOrNull()
                if (runtimeInMinutes == null) {
                    val nextEpisodeRuntime = nextEpisodeToAir?.runtime
                    val lastEpisodeRuntime = lastEpisodeToAir?.runtime
                    runtimeInMinutes =
                        if (nextEpisodeRuntime != null && lastEpisodeRuntime != null) {
                            (nextEpisodeRuntime + lastEpisodeRuntime) / 2
                        } else {
                            nextEpisodeRuntime ?: lastEpisodeRuntime
                        }
                }
                runtimeInMinutes ?: 0
            }
        }
    }

    fun hasRuntime(): Boolean {
        return displayableRuntime() > 0
    }

 
    fun getReleaseYear(): String? {
        val regex = "\\b(\\d{4})\\b".toRegex()
        if (this is DetailsTvData) {
            return regex.find(firstAirDate ?: "")?.value
        } else if (this is DetailsMovieData) {
            return regex.find(releaseDate ?: "")?.value
        }
        return null
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

@Parcelize
@Serializable
data class DetailsMovieData(
    val originalTitle: String? = null,
    val title: String? = null,
    val releaseDate: String? = null,
    val revenue: Long? = null,
    val runtime: Int? = null,
    val budget: Int? = null,
    val video: Boolean? = null,
    val belongsToCollection: CollectionData? = null,
    private val baseId: Int = 0,
    private val baseAdult: Boolean? = null,
    private val baseBackdropPath: String? = null,
    private val baseGenres: List<GenreData> = emptyList(),
    private val baseHomepage: String? = null,
    private val baseOriginalLanguage: String? = null,
    private val baseOverview: String? = null,
    private val basePopularity: Double? = null,
    private val basePosterPath: String? = null,
    private val baseProductionCompanies: List<ProductionCompanyData> = emptyList(),
    private val baseProductionCountries: List<ProductionCountryData> = emptyList(),
    private val baseSpokenLanguages: List<SpokenLanguageData> = emptyList(),
    private val baseStatus: String? = null,
    private val baseTagline: String? = null,
    private val baseVoteAverage: Double? = null,
    private val baseVoteCount: Int? = null,
    private val baseCredits: CreditsData? = null,
    private val baseVideos: VideosData? = null
) : DetailsItemData(
    baseId,
    baseAdult,
    baseBackdropPath,
    baseGenres,
    baseHomepage,
    baseOriginalLanguage,
    baseOverview,
    basePopularity,
    basePosterPath,
    baseProductionCompanies,
    baseProductionCountries,
    baseSpokenLanguages,
    baseStatus,
    baseTagline,
    baseVoteAverage,
    baseVoteCount,
    baseCredits,
    baseVideos
)

@Serializable
@Parcelize
data class DetailsTvData(
    val originalName: String? = null,
    val name: String? = null,
    val firstAirDate: String? = null,
    val lastAirDate: String? = null,
    val episodeRunTime: List<Int> = emptyList(),
    val inProduction: Boolean? = null,
    val numberOfEpisodes: Int? = null,
    val numberOfSeasons: Int? = null,
    val originCountry: List<String> = emptyList(),
    val lastEpisodeToAir: EpisodeData? = null,
    val nextEpisodeToAir: EpisodeData? = null,
    val seasons: List<SeasonData> = emptyList(),
    val networks: List<NetworkData> = emptyList(),
    private val baseId: Int = 0,
    private val baseAdult: Boolean? = null,
    private val baseBackdropPath: String? = null,
    private val baseGenres: List<GenreData> = emptyList(),
    private val baseHomepage: String? = null,
    private val baseOriginalLanguage: String? = null,
    private val baseOverview: String? = null,
    private val basePopularity: Double? = null,
    private val basePosterPath: String? = null,
    private val baseProductionCompanies: List<ProductionCompanyData> = emptyList(),
    private val baseProductionCountries: List<ProductionCountryData> = emptyList(),
    private val baseSpokenLanguages: List<SpokenLanguageData> = emptyList(),
    private val baseStatus: String? = null,
    private val baseTagline: String? = null,
    private val baseVoteAverage: Double? = null,
    private val baseVoteCount: Int? = null,
    private val baseCredits: CreditsData? = null,
    private val baseVideos: VideosData? = null
) : DetailsItemData(
    baseId,
    baseAdult,
    baseBackdropPath,
    baseGenres,
    baseHomepage,
    baseOriginalLanguage,
    baseOverview,
    basePopularity,
    basePosterPath,
    baseProductionCompanies,
    baseProductionCountries,
    baseSpokenLanguages,
    baseStatus,
    baseTagline,
    baseVoteAverage,
    baseVoteCount,
    baseCredits,
    baseVideos
)

@Serializable
@Parcelize
data class EpisodeData(
    val id: Int,
    val name: String? = null,
    val overview: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val airDate: String? = null,
    val episodeNumber: Int? = null,
    val episodeType: String? = null,
    val productionCode: String? = null,
    val runtime: Int? = null,
    val seasonNumber: Int? = null,
    val stillPath: String? = null
) : Parcelable

@Serializable
@Parcelize
data class SeasonData(
    val id: Int,
    val name: String? = null,
    val overview: String? = null,
    val airDate: String? = null,
    val episodeCount: Int? = null,
    val posterPath: String? = null,
    val seasonNumber: Int? = null,
    val voteAverage: Double? = null
) : Parcelable

@Serializable
@Parcelize
data class NetworkData(
    val id: Int,
    val name: String? = null,
    val logoPath: String? = null,
    val originCountry: String? = null
) : Parcelable

@Serializable
@Parcelize
data class CollectionData(
    val id: Int,
    val name: String? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null
) : Parcelable

@Parcelize
@Serializable
data class GenreData(
    val id: Int,
    val name: String
) : Parcelable {
    override fun toString(): String {
        return name
    }
}

@Parcelize
@Serializable
data class ProductionCompanyData(
    val id: Int,
    val logoPath: String? = null,
    val name: String,
    val originCountry: String
) : Parcelable

@Parcelize
@Serializable
data class ProductionCountryData(
    val isoCode: String,
    val name: String
) : Parcelable

@Parcelize
@Serializable
data class SpokenLanguageData(
    val englishName: String,
    val isoCode: String,
    val name: String
) : Parcelable

@Serializable
@Parcelize
data class CreditsData(
    val cast: List<CastMemberData> = emptyList(),
    val crew: List<CrewMemberData> = emptyList()
) : Parcelable

@Serializable
@Parcelize
data class CastMemberData(
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
data class CrewMemberData(
    val adult: Boolean? = null,
    val gender: Int? = null,
    val id: Int,
    val knownForDepartment: String? = null,
    val name: String? = null,
    val originalName: String? = null,
    val popularity: Float? = null,
    val profilePath: String? = null,
    val creditId: String? = null,
    val department: String? = null,
    val job: String? = null
) : Parcelable

@Serializable
@Parcelize
data class VideosData(
    val results: List<VideoResultData> = emptyList()
) : Parcelable

@Serializable
@Parcelize
data class VideoResultData(
    val iso6391: String? = null,
    val iso31661: String? = null,
    val name: String? = null,
    val key: String? = null,
    val site: String? = null,
    val size: Int? = null,
    val type: String? = null,
    val official: Boolean? = null,
    val publishedAt: String? = null,
    val id: String? = null
) : Parcelable