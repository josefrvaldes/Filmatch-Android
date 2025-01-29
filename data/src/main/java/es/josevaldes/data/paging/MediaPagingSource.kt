package es.josevaldes.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.responses.DiscoverResponse
import es.josevaldes.data.results.ApiErrorException
import es.josevaldes.data.results.ApiResult
import javax.inject.Inject

class MediaPagingSource @Inject constructor(
    private val fetchMovies: suspend (page: Int) -> ApiResult<DiscoverResponse>
) : PagingSource<Int, DiscoverItemData>() {

    private var totalPages = 1

    override fun getRefreshKey(state: PagingState<Int, DiscoverItemData>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DiscoverItemData> {
        val page = params.key ?: 1
        return when (val result = fetchMovies(page)) {
            is ApiResult.Success -> {
                val discoverMoviesResponse = result.data
                totalPages = discoverMoviesResponse.totalPages
                val items = discoverMoviesResponse.results.map { it.toAppModel() }
                LoadResult.Page(
                    data = items,
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