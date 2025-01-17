package es.josevaldes.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.responses.ItemType
import es.josevaldes.data.results.ApiErrorException
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MoviesRemoteDataSource
import javax.inject.Inject

class MoviesPagingSource @Inject constructor(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    internal var language: String? = null,
    internal var type:
    ItemType = ItemType.MOVIE
) : PagingSource<Int, DiscoverItemData>() {

    private var totalPages = 1

    override fun getRefreshKey(state: PagingState<Int, DiscoverItemData>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DiscoverItemData> {
        val page = params.key ?: 1
        return when (val result =
            moviesRemoteDataSource.getDiscoverItems(type.path, page, language = language)) {
            is ApiResult.Success -> {
                val discoverMoviesResponse = result.data
                totalPages = discoverMoviesResponse.totalPages
                LoadResult.Page(
                    data = discoverMoviesResponse.results.map { it.toAppModel() },
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page < totalPages) {
                        page + 1
                    } else {
                        null
                    }
                )
            }

            is ApiResult.Error -> {
                LoadResult.Error(ApiErrorException(result.apiError))
            }
        }
    }
}