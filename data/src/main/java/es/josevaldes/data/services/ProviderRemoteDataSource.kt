package es.josevaldes.data.services

import es.josevaldes.data.responses.GetProvidersResponse
import es.josevaldes.data.results.ApiResult
import retrofit2.http.GET
import retrofit2.http.Query

interface ProviderRemoteDataSource {

    @GET("/3/watch/providers/movie")
    suspend fun getMovieProviders(
        @Query("language") language: String? = "en-US",
        @Query("watch_region") region: String? = "us",
    ): ApiResult<GetProvidersResponse>

    @GET("/3/watch/providers/tv")
    suspend fun getTvProviders(
        @Query("language") language: String? = "en-US",
        @Query("watch_region") region: String? = "us",
    ): ApiResult<GetProvidersResponse>
}