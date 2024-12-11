package es.josevaldes.data

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.model.Movie
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.responses.MovieResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiErrorException
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieRemoteDataSource
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
class MovieResponseRepositoryTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var moviesPagingSource: MoviesPagingSource
    private lateinit var movieRemoteDataSource: MovieRemoteDataSource
    private var listOfMovies = mutableListOf<MovieResponse>()
    private val config = PagingConfig(pageSize = 5, enablePlaceholders = false)

    @Before
    fun setUp() {
        movieRemoteDataSource = mockk()
        moviesPagingSource = MoviesPagingSource(movieRemoteDataSource, "en")
        movieRepository = MovieRepository(moviesPagingSource, movieRemoteDataSource)
        for (i in 1..20) {
            listOfMovies.add(MovieResponse(id = i, title = "Movie $i"))
        }
    }

    @Test
    fun `getDiscoverMovies should return success on valid result`() = runTest {
        val resultList = listOfMovies.subList(0, config.pageSize)
        coEvery {
            movieRemoteDataSource.getDiscoverMovies(
                any(),
                any(),
                any()
            )
        } returns ApiResult.Success(
            DiscoverMoviesResponse(
                results = resultList,
                page = 1,
                totalResults = listOfMovies.size,
                totalPages = listOfMovies.size / config.pageSize
            )
        )
        val testPager = TestPager(config, moviesPagingSource)
        val result = testPager.refresh() as LoadResult.Page<Int, Movie>
        assertEquals(resultList.map { it.toAppModel() }, result.data)
    }

    @Test
    fun `getDiscoverMovies should return success after appending data`() = runTest {
        var counter = 0
        var currentSubList = mutableListOf<MovieResponse>()
        coEvery { movieRemoteDataSource.getDiscoverMovies(any(), any(), any()) } answers {
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
        assertEquals(currentSubList.map { it.toAppModel() }, result.data)
    }


    @Test
    fun `getDiscoverMovies should handle Unknown errors correctly`() = runTest {
        coEvery {
            movieRemoteDataSource.getDiscoverMovies(
                any(),
                any(),
                any()
            )
        } returns ApiResult.Error(
            ApiError.Unknown
        )
        val testPager = TestPager(config, moviesPagingSource)
        val result = testPager.refresh() as LoadResult.Error<Int, Movie>
        assertEquals(ApiErrorException(ApiError.Unknown).toString(), result.throwable.toString())
    }


    @Test
    fun `getDiscoverMovies should handle ResourceNotFound errors correctly`() = runTest {
        coEvery {
            movieRemoteDataSource.getDiscoverMovies(
                any(),
                any(),
                any()
            )
        } returns ApiResult.Error(
            ApiError.ResourceNotFound
        )
        val testPager = TestPager(config, moviesPagingSource)
        val result = testPager.refresh() as LoadResult.Error<Int, Movie>
        assertEquals(
            ApiErrorException(ApiError.ResourceNotFound).toString(),
            result.throwable.toString()
        )
    }

    @Test
    fun `findById should return success on valid result`() = runTest {
        val movie = listOfMovies[0]
        coEvery { movieRemoteDataSource.findById(any(), any()) } returns ApiResult.Success(movie)
        val resultFlow = movieRepository.findById(movie.id, "")
        resultFlow.collect {
            assertEquals(ApiResult.Success(movie.toAppModel()), it)
        }
    }

    @Test
    fun `findById should return error on invalid result`() = runTest {
        coEvery {
            movieRemoteDataSource.findById(
                any(),
                any()
            )
        } returns ApiResult.Error(ApiError.Unknown)
        val resultFlow = movieRepository.findById(1, "")
        resultFlow.collect {
            assertEquals(ApiResult.Error(ApiError.Unknown), it)
        }
    }

    @Test
    fun `findById should return error on resource not found`() = runTest {
        coEvery {
            movieRemoteDataSource.findById(
                any(),
                any()
            )
        } returns ApiResult.Error(ApiError.ResourceNotFound)
        val resultFlow = movieRepository.findById(1, "")
        resultFlow.collect {
            assertEquals(ApiResult.Error(ApiError.ResourceNotFound), it)
        }
    }
}