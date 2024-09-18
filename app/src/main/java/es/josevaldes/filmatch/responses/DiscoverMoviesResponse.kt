package es.josevaldes.filmatch.responses

import com.google.gson.annotations.SerializedName
import es.josevaldes.filmatch.model.Movie

data class DiscoverMoviesResponse(
    @SerializedName("results") val results: List<Movie>,
    @SerializedName("page") val page: Int,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("total_pages") val totalPages: Int
)