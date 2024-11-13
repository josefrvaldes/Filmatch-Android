package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.Genre
import es.josevaldes.data.repositories.GenreRepository
import es.josevaldes.data.results.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val genresRespository: GenreRepository
) : ViewModel() {

    private val _genres = MutableStateFlow<List<Genre>>(listOf())
    val genres = _genres.asStateFlow()

    init {
        getAllGenres()
    }

    private fun getAllGenres() {
        viewModelScope.launch {
            genresRespository.getAllMovieGenres().collect {
                if (it is ApiResult.Success) {
                    _genres.value = _genres.value.plus(it.data.genres)
                } else {
                    // TODO: Handle error
                }
            }

            genresRespository.getAllTvGenres().collect {
                if (it is ApiResult.Success) {
                    _genres.value = _genres.value.plus(it.data.genres)
                } else {
                    // TODO: Handle error
                }
            }
        }
    }
}