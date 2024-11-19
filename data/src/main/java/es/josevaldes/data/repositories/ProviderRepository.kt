package es.josevaldes.data.repositories

import es.josevaldes.data.extensions.mappers.toProvidersList
import es.josevaldes.data.model.Provider
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.ProviderService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import javax.inject.Inject

class ProviderRepository @Inject constructor(private val _providerService: ProviderService) {
    fun getMovieProviders(language: String, region: String): Flow<ApiResult<List<Provider>>> =
        flow {
            try {
                val result = _providerService.getMovieProviders(language, region)
                if (result is ApiResult.Success) {
                    emit(ApiResult.Success(result.data.toProvidersList()))
                } else {
                    emit(result as ApiResult.Error)
                }
            } catch (e: Exception) {
                emit(ApiResult.Error(ApiError.Unknown))
            }
        }.retryWhen { _, attempt -> attempt < 3 }
}