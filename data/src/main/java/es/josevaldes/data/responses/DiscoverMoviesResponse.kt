package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonProperty
import es.josevaldes.data.model.Movie

data class DiscoverMoviesResponse(
    @JsonProperty("results") val results: List<Movie>,
    @JsonProperty("page") val page: Int,
    @JsonProperty("total_results") val totalResults: Int,
    @JsonProperty("total_pages") val totalPages: Int
)