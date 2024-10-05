package es.josevaldes.filmatch

import androidx.paging.PagingData
import es.josevaldes.data.model.Movie
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.data.results.ApiResult
import es.josevaldes.filmatch.viewmodels.SlideMovieViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SlideMovieViewModelTest {

    private val movieRepository = mockk<MovieRepository>()
    private val viewModel = SlideMovieViewModel(movieRepository)
    private val moviesPagingSource = mockk<MoviesPagingSource>()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher()) // Set main dispatcher for testing
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset dispatcher after tests
    }

    @Test
    fun `onLikeButtonClicked should modify swipeAction to LIKE`() {
        viewModel.onLikeButtonClicked()
        assert(viewModel.swipeAction.value == SlideMovieViewModel.SwipeAction.LIKE)
    }

    @Test
    fun `onDislikeButtonClicked should modify swipeAction to DISLIKE`() {
        viewModel.onDislikeButtonClicked()
        assert(viewModel.swipeAction.value == SlideMovieViewModel.SwipeAction.DISLIKE)
    }

    @Test
    fun `clearSwipeAction should modify swipeAction to null`() {
        viewModel.clearSwipeAction()
        assert(viewModel.swipeAction.value == null)
    }

    @Test
    fun `swipeAction should be null by default`() {
        assert(viewModel.swipeAction.value == null)
    }

    @Test
    fun `swipeAction should react correctly to different changes in a row`() {
        viewModel.onLikeButtonClicked()
        assert(viewModel.swipeAction.value == SlideMovieViewModel.SwipeAction.LIKE)
        viewModel.onDislikeButtonClicked()
        assert(viewModel.swipeAction.value == SlideMovieViewModel.SwipeAction.DISLIKE)
        viewModel.clearSwipeAction()
        assert(viewModel.swipeAction.value == null)
        viewModel.onDislikeButtonClicked()
        assert(viewModel.swipeAction.value == SlideMovieViewModel.SwipeAction.DISLIKE)
        viewModel.clearSwipeAction()
        assert(viewModel.swipeAction.value == null)
        viewModel.onLikeButtonClicked()
        assert(viewModel.swipeAction.value == SlideMovieViewModel.SwipeAction.LIKE)
    }

    // TODO: this test is unfinished. We have to come back to it soon.
//    @Test
//    fun `test moviesFlow emits correct data when language changes`() = runTest {
//        // Mockear la respuesta del MovieRepository para diferentes idiomas
//        val mockMoviesEnglish = ApiResult.Success(PagingData.from(listOf(Movie(id = 1, title = "Movie 1"))))
//        val mockMoviesSpanish = ApiResult.Success(PagingData.from(listOf(Movie(id = 2, title = "Pelicula 2"))))
//
//        // Cuando el idioma es inglés
//        coEvery { movieRepository.getDiscoverMovies(any(), "en") } returns mockMoviesEnglish
//
//        // Cuando el idioma es español
//        coEvery { movieRepository.getDiscoverMovies(any(), "es") } returns mockMoviesSpanish
//
//        // Cambiamos el idioma en el ViewModel a inglés
//        viewModel.setLanguage("en")
//
//        // Verificamos la emisión del flujo cuando el idioma es inglés
//        runBlocking {
//            viewModel.moviesFlow.collectLatest { pagingData ->
//                val firstPage = pagingData.collectData(testDispatcher) // Recoge los datos del PagingData
//                assertTrue(firstPage.contains(Movie(id = 1, title = "Movie 1"))) // Verifica que esté la película en inglés
//            }
//        }
//
//        // Cambiamos el idioma a español
//        viewModel.setLanguage("es")
//
//        // Verificamos la emisión del flujo cuando el idioma es español
//        runBlocking {
//            viewModel.moviesFlow.collectLatest { pagingData ->
//                val secondPage = pagingData.collectData(testDispatcher)
//                assertTrue(secondPage.contains(Movie(id = 2, title = "Pelicula 2"))) // Verifica que esté la película en español
//            }
//        }
//    }
}