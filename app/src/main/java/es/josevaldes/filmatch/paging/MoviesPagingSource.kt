package es.josevaldes.filmatch.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import es.josevaldes.filmatch.model.Movie
import es.josevaldes.filmatch.repositories.MovieRepository
import javax.inject.Inject

class MoviesPagingSource @Inject constructor(private val movieRepository: MovieRepository) :
    PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        val result = movieRepository.getDiscoverMovies(page)
        return result.fold(
            onSuccess = { response ->
                val movies = response.results
                LoadResult.Page(
                    data = movies,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (movies.isEmpty()) null else page + 1
                )
            },
            onFailure = { error ->
                LoadResult.Error(error)
            }
        )
    }
}