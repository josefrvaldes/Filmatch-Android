package es.josevaldes.filmatch.repositories

import es.josevaldes.filmatch.network.ApiResponseHandler
import es.josevaldes.filmatch.responses.ApiErrorResponse
import es.josevaldes.filmatch.responses.DiscoverMoviesResponse
import es.josevaldes.filmatch.services.MovieService
import es.josevaldes.filmatch.utils.Either
import javax.inject.Inject

class MovieRepository @Inject constructor(private val moviesService: MovieService) {
    suspend fun getDiscoverMovies(
        page: Int,
        language: String?
    ): Either<ApiErrorResponse, DiscoverMoviesResponse> {
        return try {
            val response = moviesService.getDiscoverMovies(page, language)
            ApiResponseHandler.handleApiResponse(response)
        } catch (e: Exception) {
            Either.Left(ApiErrorResponse(false, 500, e.message ?: "Unknown error"))
        }
    }
}