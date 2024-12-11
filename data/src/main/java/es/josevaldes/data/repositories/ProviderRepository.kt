package es.josevaldes.data.repositories

import es.josevaldes.data.extensions.mappers.toProvidersList
import es.josevaldes.data.model.Provider
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.ProviderRemoteDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import javax.inject.Inject

class ProviderRepository @Inject constructor(private val _providerRemoteDataSource: ProviderRemoteDataSource) {
    fun getMovieProviders(language: String, region: String): Flow<ApiResult<List<Provider>>> =
        flow {
            try {
                coroutineScope {
                    // let's make two requests in parallel
                    val movieProvidersResult =
                        async { _providerRemoteDataSource.getMovieProviders(language, region) }
                    val tvProvidersResult =
                        async { _providerRemoteDataSource.getTvProviders(language, region) }

                    val movieProviders = movieProvidersResult.await()
                    val tvProviders = tvProvidersResult.await()

                    val movieProvidersList = if (movieProviders is ApiResult.Success) {
                        movieProviders.data.toProvidersList()
                    } else {
                        emptyList()
                    }

                    val tvProvidersList = if (tvProviders is ApiResult.Success) {
                        tvProviders.data.toProvidersList()
                    } else {
                        emptyList()
                    }

                    val mergedProviders = (movieProvidersList + tvProvidersList)
                        .distinctBy { it.id }

                    if (mergedProviders.isNotEmpty()) {
                        emit(ApiResult.Success(mergedProviders))
                    } else {
                        emit(
                            ApiResult.Error(
                                (movieProviders as? ApiResult.Error)?.apiError
                                    ?: (tvProviders as? ApiResult.Error)?.apiError
                                    ?: ApiError.Unknown
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                emit(ApiResult.Error(ApiError.Unknown))
            }
        }.retryWhen { _, attempt -> attempt < 3 }
}