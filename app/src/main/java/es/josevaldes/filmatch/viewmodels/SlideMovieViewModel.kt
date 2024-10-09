package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.Movie
import es.josevaldes.data.paging.MovieDBPagingConfig
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.repositories.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class SlideMovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _language = MutableStateFlow("en-US")

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    val moviesFlow: Flow<PagingData<Movie>> = _language.flatMapLatest { language ->
        Pager(
            config = MovieDBPagingConfig.pagingConfig,
            pagingSourceFactory = { MoviesPagingSource(movieRepository, language) }
        ).flow
            .flowOn(Dispatchers.IO)
            .cachedIn(viewModelScope)
            .onStart {
                _isLoading.value = true
            }.onCompletion {
                _isLoading.value = false
            }
    }

    fun setLanguage(language: String) {
        _language.value = language
    }

    enum class SwipeAction {
        LIKE, DISLIKE
    }

    private val _swipeAction = MutableStateFlow<SwipeAction?>(null)
    val swipeAction = _swipeAction.asStateFlow()

    fun onLikeButtonClicked() {
        _swipeAction.value = SwipeAction.LIKE
    }

    fun onDislikeButtonClicked() {
        _swipeAction.value = SwipeAction.DISLIKE
    }

    fun clearSwipeAction() {
        _swipeAction.value = null
    }
}
