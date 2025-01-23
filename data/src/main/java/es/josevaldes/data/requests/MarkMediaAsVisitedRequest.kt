package es.josevaldes.data.requests

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import es.josevaldes.data.model.DiscoverMovieData
import es.josevaldes.data.model.DiscoverTvData

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type" // Campo adicional para identificar el subtipo
)
@JsonSubTypes(
    JsonSubTypes.Type(value = MarkShowAsVisitedRequest::class, name = "show"),
    JsonSubTypes.Type(value = MarkMovieAsVisitedRequest::class, name = "movie")
)
abstract class MarkMediaAsVisitedRequest

@JsonIgnoreProperties(ignoreUnknown = true)
data class MarkShowAsVisitedRequest(
    @JsonProperty("tv_show") val tvShow: DiscoverTvData,
    @JsonProperty("status") val status: Int
) : MarkMediaAsVisitedRequest()

data class MarkMovieAsVisitedRequest(
    @JsonProperty("movie") val movie: DiscoverMovieData,
    @JsonProperty("status") val status: Int
) : MarkMediaAsVisitedRequest()