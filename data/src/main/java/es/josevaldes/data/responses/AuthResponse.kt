package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import es.josevaldes.data.model.User

@JsonIgnoreProperties(ignoreUnknown = true)
data class AuthResponse(
    @JsonProperty("message") val message: String,
    @JsonProperty("success") val success: Boolean,
    @JsonProperty("user") val user: User
)