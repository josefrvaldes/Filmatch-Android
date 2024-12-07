package es.josevaldes.filmatch.viewmodels

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.MovieFilters
import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.data.results.ApiResult
import es.josevaldes.filmatch.model.SwipeableMovie
import es.josevaldes.filmatch.utils.DeviceLocaleProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SlideMovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    deviceLocaleProvider: DeviceLocaleProvider
) : ViewModel() {

    private val _language = deviceLocaleProvider.getDeviceLocale()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _movieFilters = MutableStateFlow(MovieFilters())
    val movieFilters: MovieFilters
        get() = _movieFilters.value


    private val _movieThatWillBeObservableNext = MutableStateFlow<SwipeableMovie?>(null)
    val movieThatWillBeObservableNext = _movieThatWillBeObservableNext.asStateFlow()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var currentPage = 1

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var pages = 1


    private var counter: Int = 0

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val NUMBER_OF_VISIBLE_MOVIES = 3

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val LOADING_THRESHOLD = 5
    }

    private val _movieListFlow = MutableStateFlow<MutableList<SwipeableMovie>>(mutableListOf())

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val movieListFlow = _movieListFlow.asStateFlow()

    private val _observableMovies = MutableStateFlow<List<SwipeableMovie>>(mutableListOf())
    val observableMovies = _observableMovies.asStateFlow()

    private val _errorMessage = MutableSharedFlow<ApiResult.Error?>(1)
    val errorMessage = _errorMessage.asSharedFlow()

    init {
        loadCurrentPage()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun loadNextPage() {
        if (currentPage < pages) {
            currentPage++
            loadCurrentPage()
        }
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun loadCurrentPage() {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.getDiscoverMovies(currentPage, _language, _movieFilters.value)
                .collect { result ->
                    _isLoading.value = false
                    if (result is ApiResult.Success) {
                        pages = result.data.totalPages
                        _isLoading.value = false
                        val swipeableMovies = result.data.results.map { SwipeableMovie(it) }
                        initializeMovies(swipeableMovies)
                        _movieListFlow.value.addAll(swipeableMovies)
                        if (_observableMovies.value.isEmpty()) {
                            refillObservableList()
                            getMovieThatWillBeObservableNext()
                        }
                        return@collect
                    } else {
                        _errorMessage.emit(result as ApiResult.Error)
                    }
                }
        }
    }

    private fun refillObservableList() {
        val firstThreeMovies = _movieListFlow.value.take(NUMBER_OF_VISIBLE_MOVIES)
        _observableMovies.value = firstThreeMovies
    }


    private fun initializeMovies(allMovies: List<SwipeableMovie>) {
        allMovies.forEach { swipeableMovie ->
            if (swipeableMovie.rotation == null) {
                swipeableMovie.rotation = if (counter == 0) {
                    0f
                } else if (counter % 2 == 0) {
                    Random.nextDouble(0.0, 4.0).toFloat()
                } else {
                    Random.nextDouble(-4.0, 0.0).toFloat()
                }
                val translation = Random.nextDouble(0.0, 8.0)
                swipeableMovie.translationY = translation.toFloat()
                counter++
            }
        }
    }

    fun onSwipe() {
        if (_movieListFlow.value.isNotEmpty()) {
            _movieListFlow.value.removeAt(0)
            if (_movieListFlow.value.size < LOADING_THRESHOLD && currentPage < pages) {
                loadNextPage()
            }
            refillObservableList()
            getMovieThatWillBeObservableNext()
        }
        clearLikeButtonAction()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getMovieThatWillBeObservableNext() {
        if (_movieListFlow.value.size > NUMBER_OF_VISIBLE_MOVIES) {
            _movieThatWillBeObservableNext.value = _movieListFlow.value[NUMBER_OF_VISIBLE_MOVIES]
        } else if (_movieListFlow.value.isNotEmpty() && _movieListFlow.value.size <= NUMBER_OF_VISIBLE_MOVIES) {
            _movieThatWillBeObservableNext.value = _movieListFlow.value.last()
        } else {
            _movieThatWillBeObservableNext.value = null
        }

    }

    enum class LikeButtonAction {
        LIKE, DISLIKE
    }

    private val _likeButtonAction = MutableStateFlow<LikeButtonAction?>(null)
    val likeButtonAction = _likeButtonAction.asStateFlow()

    fun onLikeButtonClicked() {
        _likeButtonAction.value = LikeButtonAction.LIKE
    }

    fun onDislikeButtonClicked() {
        _likeButtonAction.value = LikeButtonAction.DISLIKE
    }

    fun clearLikeButtonAction() {
        _likeButtonAction.value = null
    }

    fun onNewFiltersSelected(movieFilters: MovieFilters) {
        _movieFilters.value = movieFilters
        currentPage = 1
        _movieListFlow.value = mutableListOf()
        _observableMovies.value = mutableListOf()
        loadCurrentPage()
    }
}
