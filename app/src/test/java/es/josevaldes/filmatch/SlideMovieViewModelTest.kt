package es.josevaldes.filmatch

import es.josevaldes.data.model.DiscoverMoviesData
import es.josevaldes.data.model.Movie
import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.filmatch.model.SwipeableMovie
import es.josevaldes.filmatch.utils.DeviceLocaleProvider
import es.josevaldes.filmatch.viewmodels.SlideMovieViewModel
import es.josevaldes.filmatch.viewmodels.SlideMovieViewModel.Companion.LOADING_THRESHOLD
import es.josevaldes.filmatch.viewmodels.SlideMovieViewModel.Companion.NUMBER_OF_VISIBLE_MOVIES
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class SlideMovieViewModelTest {


    private val movieRepository = mockk<MovieRepository>()
    private val deviceLocaleProvider = mockk<DeviceLocaleProvider>()
    private lateinit var viewModel: SlideMovieViewModel


    @Before
    fun setUp() {
        coEvery {
            movieRepository.getDiscoverMovies(
                any(),
                any()
            )
        } returns flowOf(ApiResult.Success(DiscoverMoviesData(listOf(), 1, 1, 1)))
        every { deviceLocaleProvider.getDeviceLocale() } returns "en-US"
        every { deviceLocaleProvider.getDeviceCountry() } returns "US"
        viewModel = SlideMovieViewModel(movieRepository, deviceLocaleProvider)
    }


    @Test
    fun `onLikeButtonClicked should modify swipeAction to LIKE`() = runTest {
        coEvery { movieRepository.getDiscoverMovies(any(), any()) } returns mockk()
        viewModel.onLikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.LIKE)
    }

    @Test
    fun `onDislikeButtonClicked should modify swipeAction to DISLIKE`() = runTest {
        coEvery { movieRepository.getDiscoverMovies(any(), any()) } returns mockk()
        viewModel.onDislikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.DISLIKE)
    }

    @Test
    fun `clearLikeButtonAction should modify likeButtonAction to null`() = runTest {
        coEvery { movieRepository.getDiscoverMovies(any(), any()) } returns mockk()
        viewModel.clearLikeButtonAction()
        assert(viewModel.likeButtonAction.value == null)
    }

    @Test
    fun `likeButtonAction should be null by default`() = runTest {
        assert(viewModel.likeButtonAction.value == null)
    }

    @Test
    fun `swipeAction should react correctly to different changes in a row`() = runTest {
        viewModel.onLikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.LIKE)
        viewModel.onDislikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.DISLIKE)
        viewModel.clearLikeButtonAction()
        assert(viewModel.likeButtonAction.value == null)
        viewModel.onDislikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.DISLIKE)
        viewModel.clearLikeButtonAction()
        assert(viewModel.likeButtonAction.value == null)
        viewModel.onLikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.LIKE)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `loadCurrentPage should return a list of movies in the flow and fill the observableMovies and load the next observable movie`() =
        runTest {
            val discoverMoviesResponse = DiscoverMoviesData(
                listOf(
                    Movie(id = 1),
                    Movie(id = 2),
                    Movie(id = 3),
                    Movie(id = 4),
                    Movie(id = 5),
                ), 1, 5, 1
            )
            coEvery {
                movieRepository.getDiscoverMovies(
                    any(),
                    any()
                )
            } returns flowOf(ApiResult.Success(discoverMoviesResponse))


            var emittedErrorMessage: ApiResult.Error? = null
            // let's listen temporarily to the error message before calling the function
            val job = viewModel.errorMessage.onEach { emittedErrorMessage = it }.launchIn(this)

            viewModel.loadCurrentPage()

            advanceUntilIdle()
            job.cancel()

            assertFalse(viewModel.isLoading.value)
            assertEquals(discoverMoviesResponse.results.size, viewModel.movieListFlow.value.size)

            // let's test that the observable movies are filled correctly
            for (i in 0 until SlideMovieViewModel.Companion.NUMBER_OF_VISIBLE_MOVIES) {
                val currentMovie = discoverMoviesResponse.results[i]
                assertEquals(
                    currentMovie.id,
                    viewModel.observableMovies.value[i].movie.id
                )
            }

            assertEquals(viewModel.pages, discoverMoviesResponse.totalPages)

            // let's test that the next observable movie is the next one in the list
            assertEquals(
                discoverMoviesResponse.results[SlideMovieViewModel.Companion.NUMBER_OF_VISIBLE_MOVIES].id,
                viewModel.movieThatWillBeObservableNext.value?.movie?.id
            )

            // let's test that there were no errors
            assertEquals(
                emittedErrorMessage,
                null
            )
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `loadCurrentPage should emit an error message when the result is not successful`() =
        runTest {
            coEvery {
                movieRepository.getDiscoverMovies(
                    any(),
                    any()
                )
            } returns flowOf(ApiResult.Error(ApiError.Unknown))

            var emittedErrorMessage: ApiResult.Error? = null
            // let's listen temporarily to the error message before calling the function
            val job = viewModel.errorMessage.onEach { emittedErrorMessage = it }.launchIn(this)

            viewModel.loadCurrentPage()

            advanceUntilIdle()
            job.cancel()

            assertFalse(viewModel.isLoading.value)
            assertEquals(0, viewModel.movieListFlow.value.size)

            // let's test that there were errors
            assertEquals(
                emittedErrorMessage,
                ApiResult.Error(ApiError.Unknown)
            )
        }

    @Test
    fun `onSwipe should remove the first movie from the list and load the next page if the list is below the threshold`() =
        runTest {
            val resultsPerPage = 10
            val movies = List(resultsPerPage) { index -> Movie(id = index) }
            val discoverMoviesResponse = DiscoverMoviesData(
                movies, 1, 20, 2
            )
            coEvery {
                movieRepository.getDiscoverMovies(
                    any(),
                    any()
                )
            } returns flowOf(ApiResult.Success(discoverMoviesResponse))

            viewModel.loadCurrentPage()

            // let's swipe all the movies right before reaching the threshold
            for (i in 0 until LOADING_THRESHOLD) {
                viewModel.onSwipe()
                assertEquals(resultsPerPage - i - 1, viewModel.movieListFlow.value.size)
            }

            // let's swipe one more time we will reach the threshold and the next page will be loaded
            viewModel.onSwipe()
            assertEquals(resultsPerPage + LOADING_THRESHOLD - 1, viewModel.movieListFlow.value.size)
        }

    @Test
    fun `getMovieThatWillBeObservableNext should chose the proper movie all the time`() = run {
        val movies = List(5) { index -> SwipeableMovie(Movie(id = index)) }
        viewModel.movieListFlow.value.addAll(movies)

        viewModel.getMovieThatWillBeObservableNext()

        // if list length > NUMBER_OF_VISIBLE_MOVIES (3), the next observable movie should be the one at index NUMBER_OF_VISIBLE_MOVIES (index 3 means fourth movie)
        assertEquals(
            movies[NUMBER_OF_VISIBLE_MOVIES],
            viewModel.movieThatWillBeObservableNext.value
        )

        // if list length == NUMBER_OF_VISIBLE_MOVIES, the next observable movie should be the last one
        viewModel.movieListFlow.value.clear()
        viewModel.movieListFlow.value.addAll(movies.subList(0, NUMBER_OF_VISIBLE_MOVIES))
        viewModel.getMovieThatWillBeObservableNext()
        assertEquals(
            movies[NUMBER_OF_VISIBLE_MOVIES - 1],
            viewModel.movieThatWillBeObservableNext.value
        )

        // if list length < NUMBER_OF_VISIBLE_MOVIES, the next observable movie should be the last one
        viewModel.movieListFlow.value.clear()
        viewModel.movieListFlow.value.addAll(movies.subList(0, NUMBER_OF_VISIBLE_MOVIES - 1))
        viewModel.getMovieThatWillBeObservableNext()
        assertEquals(
            movies[NUMBER_OF_VISIBLE_MOVIES - 2],
            viewModel.movieThatWillBeObservableNext.value
        )

        // if list length == 0, the next observable movie should be null
        viewModel.movieListFlow.value.clear()
        viewModel.getMovieThatWillBeObservableNext()
        assertEquals(
            null,
            viewModel.movieThatWillBeObservableNext.value
        )
    }


    @Test
    fun `loadNextPage increments currentPage and calls loadCurrentPage when currentPage is less than pages`() =
        runTest {
            // let's setup `currentPage` and `pages` value so `loadNextPage` will be executed
            viewModel.currentPage = 1
            viewModel.pages = 3

            // it mocks `loadCurrentPage` to verify if it's being called
            mockkObject(viewModel) // this allows us to mock functions of the viewModel
            every { viewModel.loadCurrentPage() } just Runs

            // it will increase the value because `currentPage` is less than `pages`
            viewModel.loadNextPage()
            assertEquals(2, viewModel.currentPage)
            verify(exactly = 1) { viewModel.loadCurrentPage() }

            // it will increase the value because `currentPage` is less than `pages`
            viewModel.loadNextPage()
            assertEquals(3, viewModel.currentPage)
            verify(exactly = 2) { viewModel.loadCurrentPage() }

            // it will not increase the value because `currentPage` is equal to `pages`
            viewModel.loadNextPage()
            assertEquals(3, viewModel.currentPage)
            verify(exactly = 2) { viewModel.loadCurrentPage() }

            // it will not increase the value because `currentPage` is equal to `pages`
            viewModel.loadNextPage()
            assertEquals(3, viewModel.currentPage)
            verify(exactly = 2) { viewModel.loadCurrentPage() }

            // let's deactivate the mock
            unmockkObject(viewModel)
        }


    // this test is deprecated because we no longer use the paging library for this specific use case
    // I'll keep it here for reference just in case I use the paging library in the future
//    @Test
//    fun `movies flow should return a given amount of movies after scrolling`() = runTest {
//        val listOfMovies = mutableListOf<Movie>()
//        for (i in 0 until 50) {
//            listOfMovies.add(Movie(id = i))
//        }
//        coEvery {
//            movieRepository.getDiscoverMovies(any())
//        } returns Pager(
//            config = MovieDBPagingConfig.pagingConfig, // pages are 20 items long, prefetch distance is 5, we load 2 pages initially
//            initialKey = null,
//            pagingSourceFactory = listOfMovies.asPagingSourceFactory()
//        )
//        val items = viewModel.moviesFlow
//        val itemsSnapshot = items.asSnapshot {
//            scrollTo(index = 12)
//        }
//        assertEquals(listOfMovies.subList(0, 40), itemsSnapshot)
//
//        val itemsSnapshot2 = items.asSnapshot {
//            scrollTo(index = 36)
//        }
//        assertEquals(listOfMovies, itemsSnapshot2)
//    }
}