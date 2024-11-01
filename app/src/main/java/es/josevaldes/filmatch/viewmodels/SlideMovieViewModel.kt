package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.data.results.ApiResult
import es.josevaldes.filmatch.model.SwipeableMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SlideMovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _language = MutableStateFlow("en-US")

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var currentPage = 1
    private val loadingThreshold = 5
    private var pages = 1

    private var counter: Int = 0


//    @OptIn(ExperimentalCoroutinesApi::class)
//    val _moviesFlow: Flow<PagingData<Movie>> = _language.flatMapLatest { language ->
//        movieRepository.getDiscoverMovies(language).flow
//            .cachedIn(viewModelScope)
//            .onStart {
//                _isLoading.value = true
//            }.onCompletion {
//                _isLoading.value = false
//            }
//    }

    private val _movieListFlow = MutableStateFlow<MutableList<SwipeableMovie>>(mutableListOf())
    private val _observableMovies = MutableStateFlow<List<SwipeableMovie>>(mutableListOf())
    val observableMovies = _observableMovies.asStateFlow()

    init {
        loadCurrentPage()
    }

    private fun loadNextPage() {
        if (currentPage < pages) {
            currentPage++
            loadCurrentPage()
        }
    }

    private fun loadCurrentPage() {
        viewModelScope.launch {
            _isLoading.value = true
            var shouldRetry = true
            movieRepository.getDiscoverMovies(currentPage, _language.value).collect { result ->
                _isLoading.value = false
                if (result is ApiResult.Success) {
                    pages = result.data.totalPages
                    _isLoading.value = false
                    val swipeableMovies = result.data.results.map { SwipeableMovie(it) }
                    initializeMovies(swipeableMovies)
                    _movieListFlow.value.addAll(swipeableMovies)
                    if (_observableMovies.value.isEmpty()) {
                        refillObservableList()
                    }
                } else {
                    if (shouldRetry) {
                        shouldRetry = false
                        loadCurrentPage()
                    }
                }
            }
        }
    }

    private fun refillObservableList() {
        val firstThreeMovies = _movieListFlow.value.take(3)
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
        val movie = _movieListFlow.value.firstOrNull()
        if (movie != null) {
            _movieListFlow.value.remove(movie)
            if (_movieListFlow.value.size < loadingThreshold) {
                loadNextPage()
            }
            refillObservableList()
        }
    }

    fun setLanguage(language: String) {
        _language.value = language
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

    fun clearSwipeAction() {
        _likeButtonAction.value = null
    }
}
