package es.josevaldes.data.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class GetProvidersResponse(
    @JsonProperty("results")
    val results: List<ProviderResponse>
)

data class ProviderResponse(
    @JsonProperty("display_priorities")
    val displayPriorities: Map<String, Int>,
    @JsonProperty("display_priority")
    val displayPriority: Int,
    @JsonProperty("logo_path")
    val logoPath: String?,
    @JsonProperty("provider_name")
    val providerName: String,
    @JsonProperty("provider_id")
    val providerId: Int
)