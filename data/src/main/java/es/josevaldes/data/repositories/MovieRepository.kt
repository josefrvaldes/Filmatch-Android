package es.josevaldes.data.repositories

import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class MovieRepository @Inject constructor(private val moviesService: MovieService) {
    fun getDiscoverMovies(
        page: Int,
        language: String?
    ): Flow<ApiResult<DiscoverMoviesResponse>> = flow {
        try {
            // first, let's try to load the data from the cache
//            val cachedMovies = movieDao.getMovies(page, language)
//            if (cachedMovies.isNotEmpty()) {
//                emit(ApiResult.Success(cachedMovies.toDiscoverMoviesResponse()))
//            }

            // then, let's try to load the data from the API
            val apiResult = moviesService.getDiscoverMovies(page, language)

            // If the api call is successful, we get the data, save them and emit them
            if (apiResult is ApiResult.Success) {
//                movieDao.saveMovies(apiResult.data.results) // save in cache
                emit(apiResult) // Emit new data
            } else {
                emit(apiResult) // Emit error if happened
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(ApiError.Unknown))
        }
    }.flowOn(Dispatchers.IO)
}