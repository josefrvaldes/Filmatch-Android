package es.josevaldes.data

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import es.josevaldes.data.model.Movie
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiErrorException
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MovieRepositoryTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var moviesPagingSource: MoviesPagingSource
    private lateinit var movieService: MovieService
    private var listOfMovies = mutableListOf<Movie>()
    private val config = PagingConfig(pageSize = 5, enablePlaceholders = false)

    @Before
    fun setUp() {
        movieService = mockk()
        moviesPagingSource = MoviesPagingSource(movieService, "en")
        movieRepository = MovieRepository(moviesPagingSource)
        for (i in 1..20) {
            listOfMovies.add(Movie(id = i, title = "Movie $i"))
        }
    }

    @Test
    fun `getDiscoverMovies should return success on valid result`() = runTest {
        val resultList = listOfMovies.subList(0, config.pageSize)
        coEvery { movieService.getDiscoverMovies(any(), any(), any()) } returns ApiResult.Success(
            DiscoverMoviesResponse(
                results = resultList,
                page = 1,
                totalResults = listOfMovies.size,
                totalPages = listOfMovies.size / config.pageSize
            )
        )
        val testPager = TestPager(config, moviesPagingSource)
        val result = testPager.refresh() as LoadResult.Page<Int, Movie>
        assertEquals(resultList, result.data)
    }

    @Test
    fun `getDiscoverMovies should return success after appending data`() = runTest {
        var counter = 0
        var currentSubList = mutableListOf<Movie>()
        coEvery { movieService.getDiscoverMovies(any(), any(), any()) } answers {
            currentSubList = listOfMovies.subList(
                counter * config.pageSize,
                counter * config.pageSize + config.pageSize
            )
            counter++
            ApiResult.Success(
                DiscoverMoviesResponse(
                    results = currentSubList,
                    page = counter,
                    totalResults = listOfMovies.size,
                    totalPages = listOfMovies.size / config.pageSize
                )
            )
        }
        val pager = TestPager(config, moviesPagingSource)
        val result = with(pager) {
            this.refresh()
            append()
            append()
        } as LoadResult.Page<Int, Movie>
        assertEquals(currentSubList, result.data)
    }


    @Test
    fun `should handle Unknown errors correctly`() = runTest {
        coEvery { movieService.getDiscoverMovies(any(), any(), any()) } returns ApiResult.Error(
            ApiError.Unknown
        )
        val testPager = TestPager(config, moviesPagingSource)
        val result = testPager.refresh() as LoadResult.Error<Int, Movie>
        assertEquals(ApiErrorException(ApiError.Unknown).toString(), result.throwable.toString())
    }


    @Test
    fun `should handle ResourceNotFound errors correctly`() = runTest {
        coEvery { movieService.getDiscoverMovies(any(), any(), any()) } returns ApiResult.Error(
            ApiError.ResourceNotFound
        )
        val testPager = TestPager(config, moviesPagingSource)
        val result = testPager.refresh() as LoadResult.Error<Int, Movie>
        assertEquals(
            ApiErrorException(ApiError.ResourceNotFound).toString(),
            result.throwable.toString()
        )
    }
}