package es.josevaldes.data.responses

import com.google.gson.annotations.SerializedName
import es.josevaldes.data.model.Movie

data class DiscoverMoviesResponse(
    @SerializedName("results") val results: List<Movie>,
    @SerializedName("page") val page: Int,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("total_pages") val totalPages: Int
)