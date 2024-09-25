package es.josevaldes.filmatch.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import es.josevaldes.filmatch.model.Movie
import es.josevaldes.filmatch.repositories.MovieRepository
import es.josevaldes.filmatch.utils.fold
import javax.inject.Inject

class MoviesPagingSource @Inject constructor(
    private val movieRepository: MovieRepository,
    private val language: String? = null
) :
    PagingSource<Int, Movie>() {

    private var totalPages = 1

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        val result = movieRepository.getDiscoverMovies(page, language)
        return result.fold(
            { error ->
                LoadResult.Error(Throwable(error.code.toString()))
            },
            { response ->
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
        )
    }
}