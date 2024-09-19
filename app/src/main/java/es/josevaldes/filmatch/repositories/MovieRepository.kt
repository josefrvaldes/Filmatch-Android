package es.josevaldes.filmatch.repositories

import es.josevaldes.filmatch.responses.DiscoverMoviesResponse
import es.josevaldes.filmatch.services.MoviesService
import javax.inject.Inject

class MovieRepository @Inject constructor(private val moviesService: MoviesService) {
    suspend fun getDiscoverMovies(
        page: Int,
        language: String = "en-US"
    ): Result<DiscoverMoviesResponse> {
        return try {
            val response = moviesService.getDiscoverMovies(page, language)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}