package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class GetVisitsByIdsResponse(
    @JsonProperty("visited") val visited: List<Int>
)