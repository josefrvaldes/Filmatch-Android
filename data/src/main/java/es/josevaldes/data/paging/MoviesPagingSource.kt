package es.josevaldes.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import es.josevaldes.data.model.Movie
import es.josevaldes.data.results.ApiErrorException
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import javax.inject.Inject

class MoviesPagingSource @Inject constructor(
    private val movieService: MovieService,
    internal var language: String? = null,
) : PagingSource<Int, Movie>() {

    private var totalPages = 1

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return when (val result = movieService.getDiscoverMovies(page, language)) {
            is ApiResult.Success -> {
                val response = result.data
                totalPages = response.totalPages
                LoadResult.Page(
                    data = response.results,
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