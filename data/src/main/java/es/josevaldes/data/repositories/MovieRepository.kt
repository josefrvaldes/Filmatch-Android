package es.josevaldes.data.repositories

import androidx.paging.Pager
import es.josevaldes.data.model.Movie
import es.josevaldes.data.paging.MovieDBPagingConfig
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class MovieRepository @Inject constructor(
    private val _moviesPagingSource: MoviesPagingSource,
    private val _movieService: MovieService
) {
    fun getDiscoverMovies(
        language: String?
    ): Pager<Int, Movie> {
        return Pager(
            config = MovieDBPagingConfig.pagingConfig,
            pagingSourceFactory = {
                _moviesPagingSource.apply {
                    this.language = language
                }
            }
        )
    }


    fun getDiscoverMovies(
        page: Int,
        language: String?
    ): Flow<ApiResult<DiscoverMoviesResponse>> = flow {
        try {
            val result = _movieService.getDiscoverMovies(page, language)
            if (result is ApiResult.Success) {
                emit(ApiResult.Success(result.data))
            } else {
                emit(result)
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(ApiError.Unknown))
        }
    }


    fun findById(
        id: Int,
        language: String?
    ): Flow<ApiResult<Movie>> = flow {
        try {
            val result = _movieService.findById(id, language)
            if (result is ApiResult.Success) {
                emit(ApiResult.Success(result.data))
            } else {
                emit(result)
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(ApiError.Unknown))
        }
    }
}