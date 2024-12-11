package es.josevaldes.data

import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.repositories.GenreRepository
import es.josevaldes.data.responses.GenreResponse
import es.josevaldes.data.responses.GenresListResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.GenreRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GenreRepositoryTest {

    private lateinit var genreRepository: GenreRepository
    private val genreRemoteDataSource: GenreRemoteDataSource = mockk()

    // Sample data
    private val movieGenresList =
        GenresListResponse(listOf(GenreResponse(1, "Action"), GenreResponse(2, "Comedy")))
    private val tvGenresList =
        GenresListResponse(listOf(GenreResponse(3, "Drama"), GenreResponse(4, "Sci-Fi")))

    @Before
    fun setUp() {
        genreRepository = GenreRepository(genreRemoteDataSource)
    }

    @Test
    fun `getAllMovieGenres should return success on valid result`() = runTest {
        // Mock service response
        coEvery { genreRemoteDataSource.getAllMovieGenres() } returns ApiResult.Success(
            movieGenresList
        )

        val resultFlow = genreRepository.getAllMovieGenres()
        resultFlow.collect { result ->
            assertTrue(result is ApiResult.Success)
            assertEquals(
                movieGenresList.toAppModel(),
                (result as ApiResult.Success).data
            )
        }
    }

    @Test
    fun `getAllMovieGenres should return error on service error`() = runTest {
        // Mock service response
        val error = ApiResult.Error(ApiError.ResourceNotFound)
        coEvery { genreRemoteDataSource.getAllMovieGenres() } returns error

        val resultFlow = genreRepository.getAllMovieGenres()
        resultFlow.collect { result ->
            assertTrue(result is ApiResult.Error)
            assertEquals(ApiError.ResourceNotFound, (result as ApiResult.Error).apiError)
        }
    }

    @Test
    fun `getAllTvGenres should return success on valid result`() = runTest {
        // Mock service response
        coEvery { genreRemoteDataSource.getAllTvGenres() } returns ApiResult.Success(tvGenresList)

        val resultFlow = genreRepository.getAllTvGenres()
        resultFlow.collect { result ->
            assertTrue(result is ApiResult.Success)
            assertEquals(
                tvGenresList.toAppModel(),
                (result as ApiResult.Success).data
            )
        }
    }

    @Test
    fun `getAllTvGenres should return error on service error`() = runTest {
        // Mock service response
        val error = ApiResult.Error(ApiError.Unknown)
        coEvery { genreRemoteDataSource.getAllTvGenres() } returns error

        val resultFlow = genreRepository.getAllTvGenres()
        resultFlow.collect { result ->
            assertTrue(result is ApiResult.Error)
            assertEquals(ApiError.Unknown, (result as ApiResult.Error).apiError)
        }
    }

    @Test
    fun `getAllMovieGenres should handle exceptions`() = runTest {
        // Mock service to throw exception
        coEvery { genreRemoteDataSource.getAllMovieGenres() } throws Exception("Unexpected error")

        val resultFlow = genreRepository.getAllMovieGenres()
        resultFlow.collect { result ->
            assertTrue(result is ApiResult.Error)
            assertEquals(ApiError.Unknown, (result as ApiResult.Error).apiError)
        }
    }
}