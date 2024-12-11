package es.josevaldes.data.repositories

import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.model.GenresList
import es.josevaldes.data.model.MovieType
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.GenreRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import javax.inject.Inject

class GenreRepository @Inject constructor(private val _genreRemoteDataSource: GenreRemoteDataSource) {
    private fun getAllGenres(type: MovieType): Flow<ApiResult<GenresList>> = flow {
        try {
            val result = when (type) {
                MovieType.MOVIE -> _genreRemoteDataSource.getAllMovieGenres()
                MovieType.TVSHOW -> _genreRemoteDataSource.getAllTvGenres()
            }
            if (result is ApiResult.Success) {
                emit(ApiResult.Success(result.data.toAppModel()))
            } else {
                emit(result as ApiResult.Error)
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(ApiError.Unknown))
        }
    }.retryWhen { _, attempt ->
        attempt < 3
    }

    fun getAllMovieGenres(): Flow<ApiResult<GenresList>> = getAllGenres(MovieType.MOVIE)

    fun getAllTvGenres(): Flow<ApiResult<GenresList>> = getAllGenres(MovieType.TVSHOW)
}