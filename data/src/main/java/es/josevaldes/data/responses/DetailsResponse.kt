package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName


@JsonIgnoreProperties(ignoreUnknown = true)
abstract class DetailsItemResponse(
    @JsonProperty("id") open val id: Int,
    @JsonProperty("adult") open val adult: Boolean? = null,
    @JsonProperty("backdrop_path") open val backdropPath: String? = null,
    @JsonProperty("genres") open val genres: List<GenreResponse> = emptyList(),
    @JsonProperty("homepage") open val homepage: String? = null,
    @JsonProperty("original_language") open val originalLanguage: String? = null,
    @JsonProperty("overview") open val overview: String? = null,
    @JsonProperty("popularity") open val popularity: Double? = null,
    @JsonProperty("poster_path") open val posterPath: String? = null,
    @JsonProperty("production_companies") open val productionCompanies: List<ProductionCompanyResponse> = emptyList(),
    @JsonProperty("production_countries") open val productionCountries: List<ProductionCountryResponse> = emptyList(),
    @JsonProperty("spoken_languages") open val spokenLanguages: List<SpokenLanguageResponse> = emptyList(),
    @JsonProperty("status") open val status: String? = null,
    @JsonProperty("tagline") open val tagline: String? = null,
    @JsonProperty("vote_average") open val voteAverage: Double? = null,
    @JsonProperty("vote_count") open val voteCount: Int? = null,
    @JsonProperty("credits") open val credits: CreditsResponse? = null,
    @JsonProperty("videos") open val videos: VideosResponse? = null
)


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("movie")
class DetailsMovieResponse(
    @JsonProperty("original_title") val originalTitle: String? = null,
    @JsonProperty("title") val title: String? = null,
    @JsonProperty("release_date") val releaseDate: String? = null,
    @JsonProperty("revenue") val revenue: Long? = null,
    @JsonProperty("runtime") val runtime: Int? = null,
    @JsonProperty("budget") val budget: Int? = null,
    @JsonProperty("video") val video: Boolean? = null,
    @JsonProperty("belongs_to_collection") val belongsToCollection: CollectionResponse? = null,

    @JsonProperty("id") id: Int,
    @JsonProperty("adult") adult: Boolean? = null,
    @JsonProperty("backdrop_path") backdropPath: String? = null,
    @JsonProperty("genres") genres: List<GenreResponse> = emptyList(),
    @JsonProperty("homepage") homepage: String? = null,
    @JsonProperty("original_language") originalLanguage: String? = null,
    @JsonProperty("overview") overview: String? = null,
    @JsonProperty("popularity") popularity: Double? = null,
    @JsonProperty("poster_path") posterPath: String? = null,
    @JsonProperty("production_companies") productionCompanies: List<ProductionCompanyResponse> = emptyList(),
    @JsonProperty("production_countries") productionCountries: List<ProductionCountryResponse> = emptyList(),
    @JsonProperty("spoken_languages") spokenLanguages: List<SpokenLanguageResponse> = emptyList(),
    @JsonProperty("status") status: String? = null,
    @JsonProperty("tagline") tagline: String? = null,
    @JsonProperty("vote_average") voteAverage: Double? = null,
    @JsonProperty("vote_count") voteCount: Int? = null,
    @JsonProperty("credits") credits: CreditsResponse? = null,
    @JsonProperty("videos") videos: VideosResponse? = null
) : DetailsItemResponse(
    id = id,
    adult = adult,
    backdropPath = backdropPath,
    genres = genres,
    homepage = homepage,
    originalLanguage = originalLanguage,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    productionCompanies = productionCompanies,
    productionCountries = productionCountries,
    spokenLanguages = spokenLanguages,
    status = status,
    tagline = tagline,
    voteAverage = voteAverage,
    voteCount = voteCount,
    credits = credits,
    videos = videos
)


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("tv")
class DetailsTvResponse(
    @JsonProperty("original_name") val originalName: String? = null,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("first_air_date") val firstAirDate: String? = null,
    @JsonProperty("last_air_date") val lastAirDate: String? = null,
    @JsonProperty("episode_run_time") val episodeRunTime: List<Int> = emptyList(),
    @JsonProperty("in_production") val inProduction: Boolean? = null,
    @JsonProperty("number_of_episodes") val numberOfEpisodes: Int? = null,
    @JsonProperty("number_of_seasons") val numberOfSeasons: Int? = null,
    @JsonProperty("origin_country") val originCountry: List<String> = emptyList(),
    @JsonProperty("last_episode_to_air") val lastEpisodeToAir: EpisodeResponse? = null,
    @JsonProperty("next_episode_to_air") val nextEpisodeToAir: EpisodeResponse? = null,
    @JsonProperty("seasons") val seasons: List<SeasonResponse> = emptyList(),
    @JsonProperty("networks") val networks: List<NetworkResponse> = emptyList(),

    @JsonProperty("id") id: Int,
    @JsonProperty("adult") adult: Boolean? = null,
    @JsonProperty("backdrop_path") backdropPath: String? = null,
    @JsonProperty("genres") genres: List<GenreResponse> = emptyList(),
    @JsonProperty("homepage") homepage: String? = null,
    @JsonProperty("original_language") originalLanguage: String? = null,
    @JsonProperty("overview") overview: String? = null,
    @JsonProperty("popularity") popularity: Double? = null,
    @JsonProperty("poster_path") posterPath: String? = null,
    @JsonProperty("production_companies") productionCompanies: List<ProductionCompanyResponse> = emptyList(),
    @JsonProperty("production_countries") productionCountries: List<ProductionCountryResponse> = emptyList(),
    @JsonProperty("spoken_languages") spokenLanguages: List<SpokenLanguageResponse> = emptyList(),
    @JsonProperty("status") status: String? = null,
    @JsonProperty("tagline") tagline: String? = null,
    @JsonProperty("vote_average") voteAverage: Double? = null,
    @JsonProperty("vote_count") voteCount: Int? = null,
    @JsonProperty("credits") credits: CreditsResponse? = null,
    @JsonProperty("videos") videos: VideosResponse? = null
) : DetailsItemResponse(
    id = id,
    adult = adult,
    backdropPath = backdropPath,
    genres = genres,
    homepage = homepage,
    originalLanguage = originalLanguage,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    productionCompanies = productionCompanies,
    productionCountries = productionCountries,
    spokenLanguages = spokenLanguages,
    status = status,
    tagline = tagline,
    voteAverage = voteAverage,
    voteCount = voteCount,
    credits = credits,
    videos = videos
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class EpisodeResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("overview") val overview: String? = null,
    @JsonProperty("vote_average") val voteAverage: Double? = null,
    @JsonProperty("vote_count") val voteCount: Int? = null,
    @JsonProperty("air_date") val airDate: String? = null,
    @JsonProperty("episode_number") val episodeNumber: Int? = null,
    @JsonProperty("episode_type") val episodeType: String? = null,
    @JsonProperty("production_code") val productionCode: String? = null,
    @JsonProperty("runtime") val runtime: Int? = null,
    @JsonProperty("season_number") val seasonNumber: Int? = null,
    @JsonProperty("show_id") val showId: Int? = null,
    @JsonProperty("still_path") val stillPath: String? = null
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class SeasonResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("overview") val overview: String? = null,
    @JsonProperty("air_date") val airDate: String? = null,
    @JsonProperty("episode_count") val episodeCount: Int? = null,
    @JsonProperty("poster_path") val posterPath: String? = null,
    @JsonProperty("season_number") val seasonNumber: Int? = null,
    @JsonProperty("vote_average") val voteAverage: Double? = null
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class CollectionResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("poster_path") val posterPath: String? = null,
    @JsonProperty("backdrop_path") val backdropPath: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NetworkResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("logo_path") val logoPath: String? = null,
    @JsonProperty("origin_country") val originCountry: String? = null
)


data class GenreResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String
)


data class ProductionCompanyResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("logo_path") val logoPath: String? = null,
    @JsonProperty("name") val name: String,
    @JsonProperty("origin_country") val originCountry: String
)


data class ProductionCountryResponse(
    @JsonProperty("iso_3166_1") val isoCode: String,
    @JsonProperty("name") val name: String
)


data class SpokenLanguageResponse(
    @JsonProperty("english_name") val englishName: String,
    @JsonProperty("iso_639_1") val isoCode: String,
    @JsonProperty("name") val name: String
)


data class CreditsResponse(
    @JsonProperty("cast") val cast: List<CastMemberResponse> = emptyList(),
    @JsonProperty("crew") val crew: List<CrewMemberResponse> = emptyList()
)


data class CastMemberResponse(
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
)


data class CrewMemberResponse(
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
)


data class VideosResponse(
    @JsonProperty("results") val results: List<VideoResultResponse> = emptyList()
)


data class VideoResultResponse(
    @JsonProperty("iso_639_1") val iso6391: String? = null,
    @JsonProperty("iso_3166_1") val iso31661: String? = null,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("key") val key: String? = null,
    @JsonProperty("site") val site: String? = null,
    @JsonProperty("size") val size: Int? = null,
    @JsonProperty("type") val type: String? = null,
    @JsonProperty("official") val official: Boolean? = null,
    @JsonProperty("published_at") val publishedAt: String? = null,
    @JsonProperty("id") val id: String? = null
)