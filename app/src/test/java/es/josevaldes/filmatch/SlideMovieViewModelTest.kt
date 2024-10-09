package es.josevaldes.filmatch

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import es.josevaldes.data.model.Movie
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import es.josevaldes.filmatch.viewmodels.SlideMovieViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SlideMovieViewModelTest {

    private val testDispatcher = StandardTestDispatcher()


    private val movieService = mockk<MovieService>()
    private val movieRepository = MovieRepository(movieService)
    private val viewModel = SlideMovieViewModel(movieRepository)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Set main dispatcher for testing
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset dispatcher after tests
    }

    @Test
    fun `onLikeButtonClicked should modify swipeAction to LIKE`() = runTest {
        viewModel.onLikeButtonClicked()
        assert(viewModel.swipeAction.value == SlideMovieViewModel.SwipeAction.LIKE)
    }

    @Test
    fun `onDislikeButtonClicked should modify swipeAction to DISLIKE`() = runTest {
        viewModel.onDislikeButtonClicked()
        assert(viewModel.swipeAction.value == SlideMovieViewModel.SwipeAction.DISLIKE)
    }

    @Test
    fun `clearSwipeAction should modify swipeAction to null`() = runTest {
        viewModel.clearSwipeAction()
        assert(viewModel.swipeAction.value == null)
    }

    @Test
    fun `swipeAction should be null by default`() = runTest {
        assert(viewModel.swipeAction.value == null)
    }

    @Test
    fun `swipeAction should react correctly to different changes in a row`() = runTest {
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


    @Test
    fun `test AsyncPagingDataDiffer works with dispatcher`() = runTest {
        val differ = AsyncPagingDataDiffer(
            diffCallback = MovieDiffCallback(),
            updateCallback = NoopListCallback(),
            mainDispatcher = Dispatchers.Main,
            workerDispatcher = testDispatcher
        )

        val testData = listOf(Movie(id = 1, title = "Movie 1"))

        val pagingData = PagingData.from(testData)

        differ.submitData(pagingData)

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(testData, differ.snapshot().items)
    }


    @Test
    fun `test MoviesPagingSource returns correct data`() = runTest {

        val responseEnglish = DiscoverMoviesResponse(
            page = 1,
            results = listOf(Movie(id = 1, title = "Movie 1")),
            totalPages = 1,
            totalResults = 1
        )

        coEvery { movieService.getDiscoverMovies(any(), any()) } returns ApiResult.Success(
            responseEnglish
        )

        val pagingSource = MoviesPagingSource(movieRepository, "en-US")

        val expected = PagingSource.LoadResult.Page(
            data = responseEnglish.results,
            prevKey = null,
            nextKey = null
        )

        val actual = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertEquals(expected, actual)
    }


    // TODO: this test is failing, we will have to fix it soon
    @Test
    fun `test moviesFlow emits correct data when language changes`() = runTest {
        val responseEnglish = DiscoverMoviesResponse(
            page = 1,
            results = listOf(Movie(id = 1, title = "Movie 1")),
            totalPages = 1,
            totalResults = 1
        )

        val responseSpanish = DiscoverMoviesResponse(
            page = 1,
            results = listOf(Movie(id = 1, title = "Película 1")),
            totalPages = 1,
            totalResults = 1
        )

        // Let's mock the responses for the movies in different languages
        val mockMoviesEnglish = ApiResult.Success(responseEnglish)
        val mockMoviesSpanish = ApiResult.Success(responseSpanish)

        coEvery { movieService.getDiscoverMovies(any(), "en-US") } returns mockMoviesEnglish
        coEvery { movieService.getDiscoverMovies(any(), "es-ES") } returns mockMoviesSpanish

        viewModel.setLanguage("en-US")

        val pagingData = viewModel.moviesFlow.first()
        val differ = AsyncPagingDataDiffer(
            MovieDiffCallback(),
            NoopListCallback(),
            testDispatcher,
            testDispatcher
        )
        differ.submitData(pagingData)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(differ.snapshot().items, responseEnglish.results)
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}


class NoopListCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}