package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.filmatch.model.SwipeableMovie
import es.josevaldes.filmatch.services.MoviesService
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SlideMovieViewModel @Inject constructor(
    private val moviesService: MoviesService
) : ViewModel() {
    private val _movies = MutableLiveData<MutableList<SwipeableMovie>>()
    val movies: LiveData<MutableList<SwipeableMovie>> = _movies

    fun fetchMovies() {
        viewModelScope.launch {
            val movieList = moviesService.getDiscoverMovies().results.map { SwipeableMovie(it) }
            _movies.value = movieList.toMutableList()
        }
    }
}
