package es.josevaldes.filmatch.responses

import com.google.gson.annotations.SerializedName

data class ApiErrorResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("status_code") val code: Int,
    @SerializedName("status_message") val message: String
)