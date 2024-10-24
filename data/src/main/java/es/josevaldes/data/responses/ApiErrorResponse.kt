package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiErrorResponse(
    @JsonProperty("success") val success: Boolean,
    @JsonProperty("status_code") val code: Int,
    @JsonProperty("status_message") val message: String
)