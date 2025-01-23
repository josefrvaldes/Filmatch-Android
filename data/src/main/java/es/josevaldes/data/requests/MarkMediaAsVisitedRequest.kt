package es.josevaldes.data.requests

import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.InterestStatus

data class MarkMediaAsVisitedRequest(
    val discoverItemData: DiscoverItemData,
    val interestStatus: InterestStatus
)