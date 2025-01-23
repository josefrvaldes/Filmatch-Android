package es.josevaldes.data.repositories

import androidx.paging.Pager
import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.extensions.mappers.toVisitedMediaWithItem
import es.josevaldes.data.model.ContentType
import es.josevaldes.data.model.DetailsItemData
import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.DiscoverMovieData
import es.josevaldes.data.model.DiscoverMoviesData
import es.josevaldes.data.model.DiscoverTvData
import es.josevaldes.data.model.InterestStatus
import es.josevaldes.data.model.MediaFilters
import es.josevaldes.data.paging.MediaPagingSource
import es.josevaldes.data.paging.MovieDBPagingConfig
import es.josevaldes.data.responses.MediaType
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MediaRemoteDataSource
import es.josevaldes.local.datasources.MediaLocalDataSource
import es.josevaldes.local.entities.MediaEntityType
import es.josevaldes.local.entities.VisitedFiltersEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import javax.inject.Inject


class MediaRepository @Inject constructor(
    private val _mediaPagingSource: MediaPagingSource,
    private val _mediaRemoteDataSource: MediaRemoteDataSource,
    private val _mediaLocalDataSource: MediaLocalDataSource
) {
    fun getDiscoverMovies(
        language: String?,
    ): Pager<Int, DiscoverItemData> {
        return Pager(
            config = MovieDBPagingConfig.pagingConfig,
            pagingSourceFactory = {
                _mediaPagingSource.apply {
                    this.language = language
                }
            }
        )
    }


    fun getDiscoverMovies(
        page: Int,
        language: String,
        filters: MediaFilters
    ): Flow<ApiResult<DiscoverMoviesData>> = flow {
        try {
            val country = language.substring(language.indexOf("-") + 1)
            val type = when (filters.contentType) {
                ContentType.MOVIES -> MediaType.MOVIE.path
                ContentType.TV_SHOWS -> MediaType.TV.path
            }
            val result = _mediaRemoteDataSource.getDiscoverItems(
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
                _mediaLocalDataSource.insertVisitedFiltersIfMaxPageIsHigher(visitedFiltersEntity)
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
        type: MediaType,
        language: String?
    ): Flow<ApiResult<DetailsItemData>> = flow {
        try {
            val result = _mediaRemoteDataSource.findById(id, type.path, language)
            if (result is ApiResult.Success) {
                val resultAppModel = result.data.toAppModel()
                emit(ApiResult.Success(resultAppModel))
            } else {
                emit(result as ApiResult.Error)
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(ApiError.Unknown))
        }
    }

    suspend fun markedMovieAsVisited(media: DiscoverItemData, interestStatus: InterestStatus) {
        val visitedMediaWithItem = media.toVisitedMediaWithItem(interestStatus)
        _mediaLocalDataSource.saveVisitedMedia(visitedMediaWithItem)
    }


    suspend fun isMovieVisited(movie: DiscoverItemData): Boolean {
        val type = when (movie) {
            is DiscoverMovieData -> MediaEntityType.MOVIE
            is DiscoverTvData -> MediaEntityType.TV
            else -> throw IllegalArgumentException("Unknown type")
        }
        return _mediaLocalDataSource.isMovieVisited(movie.id, type)
    }

    suspend fun getMaxPage(filters: MediaFilters): Int? {
        val hash = filters.filtersHash
        return _mediaLocalDataSource.getMaxPage(hash)
    }
}