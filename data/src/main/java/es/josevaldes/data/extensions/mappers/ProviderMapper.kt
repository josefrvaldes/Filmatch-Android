package es.josevaldes.data.extensions.mappers

import es.josevaldes.data.model.Provider
import es.josevaldes.data.responses.GetProvidersResponse
import es.josevaldes.data.responses.ProviderResponse

fun ProviderResponse.toProvider(): Provider {
    return Provider(
        id = providerId,
        name = providerName,
        logoPath = logoPath,
        displayPriority = displayPriority,
        displayPriorities = displayPriorities
    )
}

fun GetProvidersResponse.toProvidersList(): List<Provider> {
    return results.map { it.toProvider() }
}