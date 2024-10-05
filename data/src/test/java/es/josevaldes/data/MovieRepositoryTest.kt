package es.josevaldes.data

import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovieRepositoryTest {

    private lateinit var movieService: MovieService
    private lateinit var movieRepository: MovieRepository

    @Before
    fun setUp() {
        movieService = mockk()
        movieRepository = MovieRepository(movieService)
    }

    @Test
    fun `getDiscoverMovies should return success on valid result`() = runBlocking {
        coEvery { movieService.getDiscoverMovies(any(), any()) } returns ApiResult.Success(
            DiscoverMoviesResponse(
                page = 1,
                results = listOf(),
                totalPages = 10,
                totalResults = 100
            )
        )

        val resultFlow = movieRepository.getDiscoverMovies(0, "en")
        val result = resultFlow.first()
        assertTrue(result is ApiResult.Success)
        val data = (result as ApiResult.Success).data
        assertTrue(data.totalPages == 10)
        assertTrue(data.totalResults == 100)
        assertTrue(data.results.isEmpty())
    }

    @Test
    fun `any call should return ApiError Unknown on unexpected error`(): Unit = runBlocking {
        coEvery { movieService.getDiscoverMovies(any(), any()) } returns ApiResult.Error(ApiError.Unknown)
        val resultFlow = movieRepository.getDiscoverMovies(0, "en")
        val result = resultFlow.first()

        assertTrue(result is ApiResult.Error) // Let's make sure that we have an error

        val apiError = (result as ApiResult.Error).apiError
        assertTrue(apiError is ApiError.Unknown)
    }

    @Test
    fun `any call should return ApiError ResourceNotFound on not found error`(): Unit = runBlocking {
        coEvery { movieService.getDiscoverMovies(any(), any()) } returns ApiResult.Error(ApiError.ResourceNotFound)
        val resultFlow = movieRepository.getDiscoverMovies(0, "en")
        val result = resultFlow.first()

        assertTrue(result is ApiResult.Error) // Let's make sure that we have an error

        val apiError = (result as ApiResult.Error).apiError
        assertTrue(apiError is ApiError.ResourceNotFound)
    }
}