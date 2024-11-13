package es.josevaldes.data.repositories

import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.model.GenresList
import es.josevaldes.data.model.MovieType
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.GenreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import javax.inject.Inject

class GenreRepository @Inject constructor(private val _genreService: GenreService) {
    fun getAllMovieGenres(): Flow<ApiResult<GenresList>> = flow {
        try {
            val result =
                _genreService.getAllMovieGenres()
            if (result is ApiResult.Success) {
                emit(ApiResult.Success(result.data.toAppModel(MovieType.MOVIE)))
            } else {
                emit(result as ApiResult.Error)
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(ApiError.Unknown))
        }
    }.retryWhen { _, attempt ->
        attempt < 3
    }

    fun getAllTvGenres(): Flow<ApiResult<GenresList>> = flow {
        try {
            val result = _genreService.getAllTvGenres()
            if (result is ApiResult.Success) {
                emit(ApiResult.Success(result.data.toAppModel(MovieType.TVSHOW)))
            } else {
                emit(result as ApiResult.Error)
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(ApiError.Unknown))
        }
    }.retryWhen { _, attempt ->
        attempt < 3
    }
}