package es.josevaldes.filmatch.viewmodels

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.di.IoDispatcher
import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.MediaFilters
import es.josevaldes.data.repositories.MediaRepository
import es.josevaldes.data.results.ApiResult
import es.josevaldes.filmatch.model.SwipeableMovie
import es.josevaldes.filmatch.utils.DeviceLocaleProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SlideMovieViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    @IoDispatcher private val dispatcherIO: CoroutineDispatcher
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _mediaFilters = MutableStateFlow(MediaFilters())
    val mediaFilters: MediaFilters
        get() = _mediaFilters.value


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
    internal suspend fun cleanVisitedItems(movies: List<DiscoverItemData>): List<DiscoverItemData> {
        return withContext(dispatcherIO) {
            movies.filter { movie ->
                !mediaRepository.isMovieVisited(movie)
            }
        }
    }


    fun loadCurrentPage() {
        val language = deviceLocaleProvider.getDeviceLocale()
        viewModelScope.launch {
            _isLoading.value = true
            mediaRepository.getDiscoverMovies(currentPage, language, _mediaFilters.value)
                .collect { result ->
                    if (result is ApiResult.Success) {
                        pages = result.data.totalPages


                        // we have received one page, if we are in the first three pages, we will have to
                        val receivedItems = result.data.results

                        // todo: this means that there are no more results to show
                        if (receivedItems.isEmpty()) {
                            _isLoading.value = false //
                            return@collect
                        }

                        // check if the user has already visited it completely
                        val cleanedMovies = cleanVisitedItems(receivedItems)
                        if (cleanedMovies.isEmpty()) {
                            // we will visit always the first 3 pages just in case there are
                            // new movies in any of them
                            if (currentPage <= 3) {
                                loadNextPage()
                            } else {
                                // otherwise we will just skip to the max page visited
                                val maxPage = mediaRepository.getMaxPage(_mediaFilters.value)
                                maxPage?.let {
                                    // this will avoid entering in an infinite loop because
                                    // the last page has been visited completely
                                    currentPage = if (currentPage == it) {
                                        it + 1
                                    } else {
                                        it
                                    }
                                    loadCurrentPage()
                                }
                            }
                            return@collect


                            // we have received a page and we have filtered some movies, that means
                            // that the user already visited this page in the past. Since we are not
                            // sure how many movies has the user visited in the past, let's check if
                            // we should load the next page just in case.
                            // In this case, we won't force the next page to be loaded now, we will
                            // do this check instead
                        } else if (cleanedMovies.size < receivedItems.size) {
                            checkIfWeShouldLoadNextPage()
                        }

                        _isLoading.value = false

                        // otherwise, we will just show the received movies
                        val swipeableMovies = cleanedMovies.map { SwipeableMovie(it) }
                        initializeMovies(swipeableMovies)
                        _movieListFlow.value.addAll(swipeableMovies)
                        if (_observableMovies.value.isEmpty()) {
                            refillObservableList()
                            getMovieThatWillBeObservableNext()
                        }
                        return@collect
                    } else {
                        _isLoading.value = false
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

    private fun checkIfWeShouldLoadNextPage() {
        if (_movieListFlow.value.size < LOADING_THRESHOLD && currentPage < pages) {
            loadNextPage()
        }
    }

    fun onSwipe(movie: SwipeableMovie) {
        viewModelScope.launch {
            mediaRepository.markedMovieAsVisited(movie.movie, movie.swipedStatus)
        }

        if (_movieListFlow.value.isNotEmpty()) {
            _movieListFlow.value.removeAt(0)
            checkIfWeShouldLoadNextPage()
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

    fun onNewFiltersSelected(mediaFilters: MediaFilters) {
        if (mediaFilters == _mediaFilters.value) {
            return
        }
        _mediaFilters.value = mediaFilters
        currentPage = 1
        _movieListFlow.value = mutableListOf()
        _observableMovies.value = mutableListOf()
        loadCurrentPage()
    }
}
