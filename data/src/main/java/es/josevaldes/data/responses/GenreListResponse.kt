package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonProperty


data class GenresListResponse(
    @JsonProperty("genres")
    val genres: List<GenreResponse>
)