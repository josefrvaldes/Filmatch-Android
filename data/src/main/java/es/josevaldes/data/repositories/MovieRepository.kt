package es.josevaldes.data.repositories

import androidx.paging.Pager
import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.model.ContentType
import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.DiscoverMoviesData
import es.josevaldes.data.model.Movie
import es.josevaldes.data.model.MovieFilters
import es.josevaldes.data.paging.MovieDBPagingConfig
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MoviesRemoteDataSource
import es.josevaldes.local.datasources.MoviesLocalDataSource
import es.josevaldes.local.entities.VisitedFiltersEntity
import es.josevaldes.local.entities.VisitedMovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import javax.inject.Inject


class MovieRepository @Inject constructor(
    private val _moviesPagingSource: MoviesPagingSource,
    private val _moviesRemoteDataSource: MoviesRemoteDataSource,
    private val _moviesLocalDataSource: MoviesLocalDataSource
) {
    fun getDiscoverMovies(
        language: String?,
    ): Pager<Int, DiscoverItemData> {
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
        language: String,
        filters: MovieFilters
    ): Flow<ApiResult<DiscoverMoviesData>> = flow {
        try {
            val country = language.substring(language.indexOf("-") + 1)
            val type = when (filters.contentType) {
                ContentType.MOVIES -> MoviesRemoteDataSource.DiscoverType.MOVIE.path
                ContentType.TV_SHOWS -> MoviesRemoteDataSource.DiscoverType.TV.path
            }
            val result = _moviesRemoteDataSource.getDiscoverItems(
                type = type,
                page = page,
                language = language,
                sortBy = filters.sortBy,
                withGenres = filters.genres?.joinToString("|") { it.id.toString() },
                withProviders = filters.providers?.joinToString("|") { it.id.toString() },
                watchRegion = country,
                withReleaseYearFrom = filters.yearFrom.toString(),
                withReleaseYearTo = filters.yearTo.toString(),
                withVoteAverageGte = filters.score?.score,
                withDuration = filters.duration?.duration
            )
            if (result is ApiResult.Success) {
                val visitedFiltersEntity = VisitedFiltersEntity(
                    filtersHash = filters.filtersHash,
                    maxPage = page
                )
                _moviesLocalDataSource.insertVisitedFiltersIfMaxPageIsHigher(visitedFiltersEntity)
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


    fun findById(
        id: Int,
        language: String?
    ): Flow<ApiResult<Movie>> = flow {
        try {
            val result = _moviesRemoteDataSource.findById(id, language)
            if (result is ApiResult.Success) {
                emit(ApiResult.Success(result.data.toAppModel()))
            } else {
                emit(result as ApiResult.Error)
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(ApiError.Unknown))
        }
    }

    suspend fun markedMovieAsVisited(movie: DiscoverItemData) {
        val visitedMovieEntity = VisitedMovieEntity(
            id = movie.id.toString()
        )
        _moviesLocalDataSource.insertVisitedMovie(visitedMovieEntity)
    }

    suspend fun isMovieVisited(movieId: String): Boolean {
        return _moviesLocalDataSource.isMovieVisited(movieId)
    }

    suspend fun getMaxPage(filters: MovieFilters): Int? {
        val hash = filters.filtersHash
        return _moviesLocalDataSource.getMaxPage(hash)
    }
}