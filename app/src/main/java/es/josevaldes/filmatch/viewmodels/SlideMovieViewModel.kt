package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.filmatch.paging.MovieDBPagingConfig
import es.josevaldes.filmatch.paging.MoviesPagingSource
import es.josevaldes.filmatch.repositories.MovieRepository
import javax.inject.Inject

@HiltViewModel
class SlideMovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    val moviesFlow = Pager(MovieDBPagingConfig.pagingConfig) {
        MoviesPagingSource(movieRepository)
    }.flow.cachedIn(viewModelScope)
}
