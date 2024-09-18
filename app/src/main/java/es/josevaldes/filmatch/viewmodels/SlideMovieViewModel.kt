package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.filmatch.model.SwipeableMovie
import es.josevaldes.filmatch.services.MoviesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SlideMovieViewModel @Inject constructor(
    private val moviesService: MoviesService
) : ViewModel() {

    private val _movies = MutableStateFlow<List<SwipeableMovie>>(emptyList())
    val movies: StateFlow<List<SwipeableMovie>> = _movies

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun fetchMovies() {
        viewModelScope.launch {
            _loading.value = true
            val movieList = moviesService.getDiscoverMovies().results.map { SwipeableMovie(it) }
            _movies.value = movieList.toMutableList()
            _loading.value = false
        }
    }
}
