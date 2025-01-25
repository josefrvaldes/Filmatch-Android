package es.josevaldes.data.repositories

import androidx.paging.Pager
import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.extensions.mappers.toDataModel
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
import es.josevaldes.data.requests.MarkMovieAsVisitedRequest
import es.josevaldes.data.requests.MarkShowAsVisitedRequest
import es.josevaldes.data.responses.MediaType
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.FilmatchRemoteDataSource
import es.josevaldes.data.services.MediaRemoteDataSource
import es.josevaldes.local.datasources.MediaLocalDataSource
import es.josevaldes.local.entities.MediaEntityType
import es.josevaldes.local.entities.VisitedFiltersEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import timber.log.Timber
import javax.inject.Inject


class MediaRepository @Inject constructor(
    private val _mediaPagingSource: MediaPagingSource,
    private val _mediaRemoteDataSource: MediaRemoteDataSource,
    private val _mediaLocalDataSource: MediaLocalDataSource,
    private val _filmatchRemoteDataSource: FilmatchRemoteDataSource
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

    suspend fun markMovieAsVisited(media: DiscoverItemData, interestStatus: InterestStatus) {
        val mediaAsVisitedRequest = when (media) {
            is DiscoverMovieData -> {
                MarkMovieAsVisitedRequest(
                    media,
                    interestStatus.ordinal
                )
            }

            is DiscoverTvData -> {
                MarkShowAsVisitedRequest(
                    media,
                    interestStatus.ordinal
                )
            }

            else -> throw IllegalArgumentException("Couldn't convert media to request")
        }

        val result = _filmatchRemoteDataSource.markMediaAsVisited(mediaAsVisitedRequest)

        if (result is ApiResult.Success) {
            val visitedMediaWithItem = media.toVisitedMediaWithItem(interestStatus)
            _mediaLocalDataSource.saveVisitedMedia(visitedMediaWithItem)
            Timber.d("Marked media as visited locally and remotely: $result")
        } else {
            Timber.e("Failed to mark media as visited remotely: $result")
        }
    }

    fun getVisitsByIds(medias: List<DiscoverItemData>): Flow<ApiResult<List<Int>>> =
        flow {
            val ids = medias.map { it.id }.joinToString(",")
            val result = when (medias.first()) {
                is DiscoverMovieData -> _filmatchRemoteDataSource.getMovieVisitsByIds(ids)
                is DiscoverTvData -> _filmatchRemoteDataSource.getTvVisitsByIds(ids)
                else -> throw IllegalArgumentException("Unknown type")
            }
            if (result is ApiResult.Success) {
                emit(ApiResult.Success(result.data.visited))
            } else {
                emit(result as ApiResult.Error)
            }
        }.catch { e ->
            e.printStackTrace()
            emit(ApiResult.Error(ApiError.Unknown))
        }

    private suspend fun getMediaVisitStatus(media: DiscoverItemData): InterestStatus? {
        val type = when (media) {
            is DiscoverMovieData -> MediaEntityType.MOVIE
            is DiscoverTvData -> MediaEntityType.TV
            else -> throw IllegalArgumentException("Unknown type")
        }

        val result = _mediaLocalDataSource.getMediaStatus(media.id, type)
        if (result == null) {
            val apiResult = when (type) {
                MediaEntityType.MOVIE -> _filmatchRemoteDataSource.getMovieVisitStatus(
                    media.id
                )

                MediaEntityType.TV -> _filmatchRemoteDataSource.getTvVisitStatus(media.id)
            }
            if (apiResult is ApiResult.Success) {
                return apiResult.data.interestStatus()
            }
        }
        return result?.toDataModel()
    }

    suspend fun isMovieVisited(movie: DiscoverItemData): Boolean {
        val status = getMediaVisitStatus(movie)
        return status != null
    }

    suspend fun getMaxPage(filters: MediaFilters): Int? {
        val hash = filters.filtersHash
        return _mediaLocalDataSource.getMaxPage(hash)
    }
}