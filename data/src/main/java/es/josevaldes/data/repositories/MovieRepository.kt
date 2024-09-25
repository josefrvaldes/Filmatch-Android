package es.josevaldes.data.repositories

import es.josevaldes.core.utils.Either
import es.josevaldes.data.network.ApiResponseHandler
import es.josevaldes.data.responses.ApiErrorResponse
import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.services.MovieService
import retrofit2.HttpException
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
            val code = if (e is HttpException) e.code() else 505
            Either.Left(ApiErrorResponse(false, code, e.message ?: "Unknown error"))
        }
    }
}