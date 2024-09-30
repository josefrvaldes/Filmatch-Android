package es.josevaldes.data

import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
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
    fun `getDiscoverMovies should return ApiError on any api error`(): Unit = runBlocking {
        val errorJson = """
            {
                "success": false,
                "status_code": 22,
                "status_message": "Invalid page: Pages start at 1 and max at 500. They are expected to be an integer."
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody(errorJson)
        )

        val result = movieRepository.getDiscoverMovies(0, "en")

        assertTrue(result is ApiResult.Error) // Let's make sure that we have an error

        val apiError = (result as ApiResult.Error).apiError
        assertTrue(apiError is ApiError.InvalidPage)
        assertTrue(apiError.message == "Invalid page: Pages start at 1 and max at 500. They are expected to be an integer.")
    }

    @Test
    fun `getDiscoverMovies should return WhateverResponse on any valid response`(): Unit =
        runBlocking {
            val responseJson = """
            {
    "page": 1,
    "results": [
        {
            "adult": false,
            "backdrop_path": "/Asg2UUwipAdE87MxtJy7SQo08XI.jpg",
            "genre_ids": [
                28,
                14,
                27
            ],
            "id": 957452,
            "original_language": "en",
            "original_title": "The Crow",
            "overview": "Un año después de que él y su prometida fueran asesinados, un cuervo místico devuelve a Eric a la vida para que pueda clamar su venganza.",
            "popularity": 2505.846,
            "poster_path": "/X9iFHeIYgfqoZImvdidx8b9v4R.jpg",
            "release_date": "2024-08-21",
            "title": "El Cuervo",
            "video": false,
            "vote_average": 5.444,
            "vote_count": 339
        }
    ],
    "total_pages": 1,
    "total_results": 1
}
        """.trimIndent()

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(responseJson)
            )

            val result = movieRepository.getDiscoverMovies(0, "en")

            assertTrue(result is ApiResult.Success) // Let's make sure that we have a success

            val discoverResult = (result as ApiResult.Success).data
            assertTrue(discoverResult.totalPages == 1)
            assertTrue(discoverResult.totalResults == 1)
            assertTrue(discoverResult.page == 1)
            assertTrue(discoverResult.results.size == 1)
            assertTrue(discoverResult.results.first().originalTitle == "The Crow")
        }
}