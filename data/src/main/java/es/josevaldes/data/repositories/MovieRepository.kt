package es.josevaldes.data.repositories

import es.josevaldes.data.network.ApiResponseHandler
import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import javax.inject.Inject


class MovieRepository @Inject constructor(private val moviesService: MovieService) {
    suspend fun getDiscoverMovies(
        page: Int,
        language: String?
    ): ApiResult<DiscoverMoviesResponse> {
        return try {
            val response = moviesService.getDiscoverMovies(page, language)
            ApiResponseHandler.handleApiResponse(response)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown)
        }
    }
}