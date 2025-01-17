package es.josevaldes.data

import es.josevaldes.data.model.Provider
import es.josevaldes.data.repositories.ProviderRepository
import es.josevaldes.data.responses.GetProvidersResponse
import es.josevaldes.data.responses.ProviderResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.ProviderRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class ProviderRepositoryTest {

    private lateinit var providerRepository: ProviderRepository
    private val providerRemoteDataSource: ProviderRemoteDataSource = mockk()

    // Sample data
    private val movieProvidersResponse =
        GetProvidersResponse(
            listOf(
                ProviderResponse(emptyMap(), 1, "netflix.png", "Netflix", 1),
                ProviderResponse(emptyMap(), 2, "hbo.png", "HBO", 2)
            )
        )
    private val tvProvidersResponse =
        GetProvidersResponse(
            listOf(
                ProviderResponse(emptyMap(), 1, "netflix.png", "Netflix", 1),
                ProviderResponse(emptyMap(), 3, "disney.png", "Disney+", 3)
            )
        )

    @Before
    fun setUp() {
        providerRepository = ProviderRepository(providerRemoteDataSource)
    }

    @Test
    fun `getMovieProviders should return merged providers on success`() = runTest {
        // Mock service responses
        coEvery {
            providerRemoteDataSource.getMovieProviders(
                "en",
                "US"
            )
        } returns ApiResult.Success(
            movieProvidersResponse
        )
        coEvery { providerRemoteDataSource.getTvProviders("en", "US") } returns ApiResult.Success(
            tvProvidersResponse
        )


        val resultFlow = providerRepository.getMovieProviders("en", "US")
        val expectedResult = listOf(
            Provider(1, "Netflix", "netflix.png", 1, emptyMap()),
            Provider(2, "HBO", "hbo.png", 2, emptyMap()),
            Provider(3, "Disney+", "disney.png", 3, emptyMap())
        ).toList()

        resultFlow.collect { result ->
            assertTrue(result is ApiResult.Success)
            val realResult = (result as ApiResult.Success).data.toList()
            assertEquals(
                expectedResult.toList(),
                realResult.toList()
            )
        }
    }

    @Test
    fun `getMovieProviders should return error if both responses fail`() = runTest {
        // Mock service responses
        coEvery {
            providerRemoteDataSource.getMovieProviders(
                "en",
                "US"
            )
        } returns ApiResult.Error(ApiError.ResourceNotFound)
        coEvery {
            providerRemoteDataSource.getTvProviders(
                "en",
                "US"
            )
        } returns ApiResult.Error(ApiError.ResourceNotFound)

        val resultFlow = providerRepository.getMovieProviders("en", "US")
        resultFlow.collect { result ->
            assertTrue(result is ApiResult.Error)
            assertEquals(ApiError.ResourceNotFound, (result as ApiResult.Error).apiError)
        }
    }

    @Test
    fun `getMovieProviders should return error if both responses are empty`() = runTest {
        // Mock service responses
        coEvery {
            providerRemoteDataSource.getMovieProviders(
                "en",
                "US"
            )
        } returns ApiResult.Success(
            GetProvidersResponse(emptyList())
        )
        coEvery { providerRemoteDataSource.getTvProviders("en", "US") } returns ApiResult.Success(
            GetProvidersResponse(emptyList())
        )

        val resultFlow = providerRepository.getMovieProviders("en", "US")
        resultFlow.collect { result ->
            assertTrue(result is ApiResult.Error)
            assertEquals(ApiError.Unknown, (result as ApiResult.Error).apiError)
        }
    }

    @Test
    fun `getMovieProviders should return success with partial data if one response fails`() =
        runTest {
            // Mock service responses
            coEvery {
                providerRemoteDataSource.getMovieProviders(
                    "en",
                    "US"
                )
            } returns ApiResult.Success(
                movieProvidersResponse
            )
            coEvery {
                providerRemoteDataSource.getTvProviders(
                    "en",
                    "US"
                )
            } returns ApiResult.Error(ApiError.ResourceNotFound)


            val resultFlow = providerRepository.getMovieProviders("en", "US")
            resultFlow.collect { result ->
                assertTrue(result is ApiResult.Success)
                assertEquals(
                    listOf(
                        Provider(1, "Netflix", "netflix.png", 1, emptyMap()),
                        Provider(2, "HBO", "hbo.png", 2, emptyMap())
                    ),
                    (result as ApiResult.Success).data
                )
            }
        }

    @Test
    fun `getMovieProviders should handle exceptions`() = runTest {
        // Mock service to throw an exception
        coEvery {
            providerRemoteDataSource.getMovieProviders(
                "en",
                "US"
            )
        } throws Exception("Unexpected error")
        coEvery {
            providerRemoteDataSource.getTvProviders(
                "en",
                "US"
            )
        } throws Exception("Unexpected error")

        val resultFlow = providerRepository.getMovieProviders("en", "US")
        resultFlow.collect { result ->
            assertTrue(result is ApiResult.Error)
            assertEquals(ApiError.Unknown, (result as ApiResult.Error).apiError)
        }
    }
}