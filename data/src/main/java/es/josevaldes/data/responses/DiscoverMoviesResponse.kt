package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class DiscoverMoviesResponse(
    @JsonProperty("results") val results: List<MovieResponse>,
    @JsonProperty("page") val page: Int,
    @JsonProperty("total_results") val totalResults: Int,
    @JsonProperty("total_pages") val totalPages: Int
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class MovieResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("adult") val adult: Boolean? = null,
    @JsonProperty("backdrop_path") val backdropPath: String? = null,
    @JsonProperty("belongs_to_collection") val belongsToCollection: CollectionResponse? = null,
    @JsonProperty("budget") val budget: Int? = null,
    @JsonProperty("genres") val genres: List<GenreResponse> = emptyList(),
    @JsonProperty("homepage") val homepage: String? = null,
    @JsonProperty("imdb_id") val imdbId: String? = null,
    @JsonProperty("origin_country") val originCountry: List<String>? = emptyList(),
    @JsonProperty("original_language") val originalLanguage: String? = null,
    @JsonProperty("original_title") val originalTitle: String? = null,
    @JsonProperty("overview") val overview: String? = null,
    @JsonProperty("popularity") val popularity: Double? = null,
    @JsonProperty("poster_path") val posterPath: String? = null,
    @JsonProperty("production_companies") val productionCompanies: List<ProductionCompanyResponse>? = emptyList(),
    @JsonProperty("production_countries") val productionCountries: List<ProductionCountryResponse>? = emptyList(),
    @JsonProperty("release_date") val releaseDate: String? = null,
    @JsonProperty("revenue") val revenue: Long? = null,
    @JsonProperty("runtime") val runtime: Int? = null,
    @JsonProperty("spoken_languages") val spokenLanguages: List<SpokenLanguageResponse>? = emptyList(),
    @JsonProperty("status") val status: String? = null,
    @JsonProperty("tagline") val tagline: String? = null,
    @JsonProperty("title") val title: String? = null,
    @JsonProperty("video") val video: Boolean? = null,
    @JsonProperty("vote_average") val voteAverage: Double? = null,
    @JsonProperty("vote_count") val voteCount: Int? = null,
    @JsonProperty("credits") val credits: CreditsResponse? = null,
    @JsonProperty("videos") val videos: VideosResponse? = null
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class CollectionResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("poster_path") val posterPath: String? = null,
    @JsonProperty("backdrop_path") val backdropPath: String? = null
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