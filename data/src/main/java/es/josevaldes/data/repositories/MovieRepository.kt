package es.josevaldes.data.repositories

import androidx.paging.Pager
import es.josevaldes.data.model.Movie
import es.josevaldes.data.paging.MovieDBPagingConfig
import es.josevaldes.data.paging.MoviesPagingSource
import javax.inject.Inject


class MovieRepository @Inject constructor(
    private val _moviesPagingSource: MoviesPagingSource
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
}