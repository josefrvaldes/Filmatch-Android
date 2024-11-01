package es.josevaldes.filmatch

import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.filmatch.viewmodels.SlideMovieViewModel
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SlideMovieViewModelTest {


    private val movieRepository = mockk<MovieRepository>()
    private val viewModel = SlideMovieViewModel(movieRepository)


    @Before
    fun setUp() {
        viewModel.setLanguage("en-US")
    }


    @Test
    fun `onLikeButtonClicked should modify swipeAction to LIKE`() = runTest {
        viewModel.onLikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.LIKE)
    }

    @Test
    fun `onDislikeButtonClicked should modify swipeAction to DISLIKE`() = runTest {
        viewModel.onDislikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.DISLIKE)
    }

    @Test
    fun `clearSwipeAction should modify swipeAction to null`() = runTest {
        viewModel.clearSwipeAction()
        assert(viewModel.likeButtonAction.value == null)
    }

    @Test
    fun `swipeAction should be null by default`() = runTest {
        assert(viewModel.likeButtonAction.value == null)
    }

    @Test
    fun `swipeAction should react correctly to different changes in a row`() = runTest {
        viewModel.onLikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.LIKE)
        viewModel.onDislikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.DISLIKE)
        viewModel.clearSwipeAction()
        assert(viewModel.likeButtonAction.value == null)
        viewModel.onDislikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.DISLIKE)
        viewModel.clearSwipeAction()
        assert(viewModel.likeButtonAction.value == null)
        viewModel.onLikeButtonClicked()
        assert(viewModel.likeButtonAction.value == SlideMovieViewModel.LikeButtonAction.LIKE)
    }


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