package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.DetailsItemData
import es.josevaldes.data.model.DetailsMovieData
import es.josevaldes.data.model.DetailsTvData
import es.josevaldes.data.repositories.MediaRepository
import es.josevaldes.data.responses.MediaType
import es.josevaldes.data.results.ApiResult
import es.josevaldes.filmatch.utils.DeviceLocaleProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    localeProvider: DeviceLocaleProvider
) : ViewModel() {

    private val _language = localeProvider.getDeviceLocale()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _movie = MutableStateFlow<DetailsItemData?>(null)
    val movie = _movie.asStateFlow()

    fun getMovieById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val type = when (_movie.value) {
                is DetailsMovieData -> MediaType.MOVIE
                is DetailsTvData -> MediaType.TV
                else -> throw IllegalStateException("Unknown movie type")
            }
            mediaRepository.findById(id, type, _language).onStart {
                _isLoading.value = true
            }.onCompletion {
                _isLoading.value = false
            }.collect {
                if (it is ApiResult.Success) {
                    _movie.value = it.data
                } else {
                    // TODO: Handle error
                }
            }
        }
    }

    fun setInitialMovie(movie: DetailsItemData) {
        _movie.value = movie
    }

}
