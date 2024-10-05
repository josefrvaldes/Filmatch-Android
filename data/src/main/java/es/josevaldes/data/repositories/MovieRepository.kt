package es.josevaldes.data.repositories

import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import javax.inject.Inject


class MovieRepository @Inject constructor(private val moviesService: MovieService) {
    suspend fun getDiscoverMovies(
        page: Int,
        language: String?
    ): ApiResult<DiscoverMoviesResponse> {
        return moviesService.getDiscoverMovies(page, language)
    }
}