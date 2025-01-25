package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class MarkMediaAsVisitedResponse(
    @JsonProperty("message") val message: String
)