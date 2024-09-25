package es.josevaldes.filmatch

import es.josevaldes.filmatch.repositories.MovieRepository
import es.josevaldes.filmatch.services.MovieService
import es.josevaldes.filmatch.utils.fold
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var movieService: MovieService
    private lateinit var movieRepository: MovieRepository

    @Before
    fun setUp() {
        // let's initialize a MockWebServer that will return whatever we want to the service
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Crear un MovieService con pointing to our mockwebserver
        movieService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieService::class.java)

        // let's init the repo
        movieRepository = MovieRepository(movieService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getDiscoverMovies should return ApiError on 404`() = runBlocking {
        val errorJson = """
            {
                "success": false,
                "status_code": 22,
                "status_message": "Invalid page: Pages start at 1 and max at 500."
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody(errorJson)
        )

        val result = movieRepository.getDiscoverMovies(0, "en")

        assertTrue(result.isLeft()) // Let's make sure that we have an error

        result.fold(
            { apiError ->
                assertTrue(apiError.code == 22)
                assertTrue(apiError.message == "Invalid page: Pages start at 1 and max at 500.")
            },
            {
                throw AssertionError("Expected error but got success")
            }
        )
    }
}