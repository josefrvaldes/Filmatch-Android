package es.josevaldes.data.repositories

import androidx.paging.Pager
import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.model.ContentType
import es.josevaldes.data.model.DiscoverMoviesData
import es.josevaldes.data.model.Movie
import es.josevaldes.data.model.MovieFilters
import es.josevaldes.data.model.MovieType
import es.josevaldes.data.paging.MovieDBPagingConfig
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
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
        language: String?,
        filters: MovieFilters
    ): Flow<ApiResult<DiscoverMoviesData>> = flow {
        try {
            val result = when (filters.contentType) {
                ContentType.MOVIES -> _movieService.getDiscoverMovies(
                    page = page,
                    language = language,
                    sortBy = filters.sortBy,
                    withGenres = filters.genres?.joinToString(",") { it.id.toString() },
                    withProviders = filters.providers?.joinToString(",") { it.id.toString() },
                    withReleaseYearFrom = filters.yearFrom.toString(),
                    withReleaseYearTo = filters.yearTo.toString(),
                    withVoteAverageGte = filters.score?.score,
                    withDuration = filters.duration?.duration
                )

                ContentType.TV_SHOWS -> _movieService.getDiscoverTV(
                    page = page,
                    language = language,
                    sortBy = filters.sortBy,
                    withGenres = filters.genres?.joinToString(",") { it.id.toString() },
                    withProviders = filters.providers?.joinToString(",") { it.id.toString() },
                    withReleaseYearFrom = filters.yearFrom.toString(),
                    withReleaseYearTo = filters.yearTo.toString(),
                    withVoteAverageGte = filters.score?.score,
                    withDuration = filters.duration?.duration
                )
            }
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


    fun findById(
        id: Int,
        language: String?
    ): Flow<ApiResult<Movie>> = flow {
        try {
            val result = _movieService.findById(id, language)
            if (result is ApiResult.Success) {
                emit(ApiResult.Success(result.data.toAppModel()))
            } else {
                emit(result as ApiResult.Error)
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(ApiError.Unknown))
        }
    }
}