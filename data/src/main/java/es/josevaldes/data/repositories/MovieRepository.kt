package es.josevaldes.data.repositories

import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class MovieRepository @Inject constructor(private val moviesService: MovieService) {
    fun getDiscoverMovies(
        page: Int,
        language: String?
    ): Flow<ApiResult<DiscoverMoviesResponse>> = flow {
        try {
            val apiResult = moviesService.getDiscoverMovies(page, language)
            emit(apiResult)
        } catch (e: Exception) {
            if (e is CancellationException) throw e // this is necessary for paging to work
            emit(ApiResult.Error(ApiError.Unknown))
        }
    }
}