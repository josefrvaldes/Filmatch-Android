package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonProperty
import es.josevaldes.data.model.InterestStatus

data class GetVisitStatusResponse(
    @JsonProperty("status") val status: Int
) {
    fun interestStatus(): InterestStatus = InterestStatus.fromInt(status)
}