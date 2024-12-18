package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import es.josevaldes.data.deserializers.DiscoverResponseDeserializer

@JsonDeserialize(using = DiscoverResponseDeserializer::class)
data class DiscoverResponse(
    @JsonProperty("results") val results: List<DiscoverItem>,
    @JsonProperty("page") val page: Int,
    @JsonProperty("total_results") val totalResults: Int,
    @JsonProperty("total_pages") val totalPages: Int
)


@JsonIgnoreProperties(ignoreUnknown = true)
abstract class DiscoverItem(
    @JsonProperty("id") open val id: Int = 0,
    @JsonProperty("adult") open val adult: Boolean? = null,
    @JsonProperty("backdrop_path") open val backdropPath: String? = null,
    @JsonProperty("genre_ids") open val genreIds: List<Int> = emptyList(),
    @JsonProperty("original_language") open val originalLanguage: String? = null,
    @JsonProperty("poster_path") open val posterPath: String? = null,
    @JsonProperty("overview") open val overview: String? = null,
    @JsonProperty("vote_average") open val voteAverage: Double? = null,
    @JsonProperty("vote_count") open val voteCount: Int? = null,
    @JsonProperty("popularity") open val popularity: Double? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
class DiscoverMovie(
    @JsonProperty("original_title") var originalTitle: String? = null,
    @JsonProperty("title") var title: String? = null,
    @JsonProperty("release_date") var releaseDate: String? = null,
    @JsonProperty("video") var video: Boolean? = null,
    @JsonProperty("id") id: Int = 0,
    @JsonProperty("adult") adult: Boolean? = null,
    @JsonProperty("backdrop_path") backdropPath: String? = null,
    @JsonProperty("genre_ids") genreIds: List<Int> = emptyList(),
    @JsonProperty("original_language") originalLanguage: String? = null,
    @JsonProperty("poster_path") posterPath: String? = null,
    @JsonProperty("overview") overview: String? = null,
    @JsonProperty("vote_average") voteAverage: Double? = null,
    @JsonProperty("vote_count") voteCount: Int? = null,
    @JsonProperty("popularity") popularity: Double? = null,
) : DiscoverItem(
    id = id,
    adult = adult,
    backdropPath = backdropPath,
    genreIds = genreIds,
    originalLanguage = originalLanguage,
    posterPath = posterPath,
    overview = overview,
    voteAverage = voteAverage,
    voteCount = voteCount,
    popularity = popularity
)


@JsonIgnoreProperties(ignoreUnknown = true)
class DiscoverTV(
    @JsonProperty("original_name") var originalName: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("first_air_date") var firstAirDate: String? = null,
    @JsonProperty("origin_country") var originCountry: List<String>? = emptyList(),
    @JsonProperty("id") id: Int = 0,
    @JsonProperty("adult") adult: Boolean? = null,
    @JsonProperty("backdrop_path") backdropPath: String? = null,
    @JsonProperty("genre_ids") genreIds: List<Int> = emptyList(),
    @JsonProperty("original_language") originalLanguage: String? = null,
    @JsonProperty("poster_path") posterPath: String? = null,
    @JsonProperty("overview") overview: String? = null,
    @JsonProperty("vote_average") voteAverage: Double? = null,
    @JsonProperty("vote_count") voteCount: Int? = null,
    @JsonProperty("popularity") popularity: Double? = null,
) : DiscoverItem(
    id = id,
    adult = adult,
    backdropPath = backdropPath,
    genreIds = genreIds,
    originalLanguage = originalLanguage,
    posterPath = posterPath,
    overview = overview,
    voteAverage = voteAverage,
    voteCount = voteCount,
    popularity = popularity
)