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
open class DiscoverItem {
    @JsonProperty("id")
    var id: Int = 0

    @JsonProperty("adult")
    var adult: Boolean? = null

    @JsonProperty("backdrop_path")
    var backdropPath: String? = null

    @JsonProperty("genre_ids")
    var genreIds: List<Int> = emptyList()

    @JsonProperty("original_language")
    var originalLanguage: String? = null

    @JsonProperty("poster_path")
    var posterPath: String? = null

    @JsonProperty("overview")
    var overview: String? = null

    @JsonProperty("vote_average")
    var voteAverage: Double? = null

    @JsonProperty("vote_count")
    var voteCount: Int? = null

    @JsonProperty("popularity")
    var popularity: Double? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class DiscoverMovie : DiscoverItem() {
    @JsonProperty("original_title")
    var originalTitle: String? = null

    @JsonProperty("title")
    var title: String? = null

    @JsonProperty("release_date")
    var releaseDate: String? = null

    @JsonProperty("video")
    var video: Boolean? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
class DiscoverTV : DiscoverItem() {
    @JsonProperty("original_name")
    var originalName: String? = null

    @JsonProperty("name")
    var name: String? = null

    @JsonProperty("first_air_date")
    var firstAirDate: String? = null

    @JsonProperty("origin_country")
    var originCountry: List<String>? = emptyList()
}