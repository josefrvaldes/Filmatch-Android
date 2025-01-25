package es.josevaldes.data

import es.josevaldes.data.adapters.ApiResultCallAdapterFactory
import es.josevaldes.data.responses.DiscoverMovie
import es.josevaldes.data.responses.MediaType
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MediaRemoteDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var mediaRemoteDataSource: MediaRemoteDataSource

    @Before
    fun setUp() {
        // let's mock the json responses that can be returned by the server
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // let's create the service
        mediaRemoteDataSource = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(JacksonConverterFactory.create(es.josevaldes.data.di.JsonMapper.objectMapper))
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .build()
            .create(MediaRemoteDataSource::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `any call should return ApiError on any api error`(): Unit = runBlocking {
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

        val response = mediaRemoteDataSource.getDiscoverItems(
            MediaType.MOVIE.path,
            0,
            "en"
        )

        assertTrue(response is ApiResult.Error) // Let's make sure that we have an error
        val apiError = (response as ApiResult.Error).apiError
        assertTrue(apiError is ApiError.InvalidPage)
        assertTrue(apiError.message == "Invalid page: Pages start at 1 and max at 500. They are expected to be an integer.")
    }

    @Test
    fun `any call should return WhateverResponse on any valid response`(): Unit =
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

            val response = mediaRemoteDataSource.getDiscoverItems(
                MediaType.MOVIE.path,
                0, "en"
            )



            assertTrue(response is ApiResult.Success) // Let's make sure that we have an error
            val discoverResult = (response as ApiResult.Success).data
            assertTrue(discoverResult.totalPages == 1)
            assertTrue(discoverResult.totalResults == 1)
            assertTrue(discoverResult.page == 1)
            assertTrue(discoverResult.results.size == 1)
            assertTrue((discoverResult.results.first() as DiscoverMovie).originalTitle == "The Crow")
        }


    @Test
    fun `any call should return HTTP error response, when response code error such as 404 and totally unexpected response such as HTML`(): Unit =
        runBlocking {
            val responseJson = """
            <html>
                <head>
                    <title>404 Not Found</title>
                </head>
                <body>
                    <h1>Not Found</h1>
                    <p>The requested URL was not found on this server.</p>
                </body>
            </html>
        """.trimIndent()

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(404)
                    .setBody(responseJson)
            )

            val response = mediaRemoteDataSource.getDiscoverItems(
                MediaType.MOVIE.path,
                0,
                "en",
            )

            assertTrue(response is ApiResult.Error) // Let's make sure that we have an error

            val apiError = (response as ApiResult.Error).apiError
            assertEquals(apiError, ApiError.ResourceNotFound)
        }

    @Test
    fun `any call should return error when HTTP code is 200 but the content is totally wrong`() =
        runBlocking {
            val responseJson = """
            <html>
                <head>
                    <title>404 Not Found</title>
                </head>
                <body>
                    <h1>Not Found</h1>
                    <p>The requested URL was not found on this server.</p>
                </body>
            </html>
        """.trimIndent()

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(responseJson)
            )

            val response = mediaRemoteDataSource.getDiscoverItems(
                MediaType.MOVIE.path,
                0,
                "en",
            )

            assertTrue(response is ApiResult.Error) // Let's make sure that we have an error
            val apiError = (response as ApiResult.Error).apiError
            assertTrue(apiError is ApiError.Unknown)
        }


    @Test
    fun `test successful response with correct JSON`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{
                "page": 1,
                "results": [],
                "total_pages": 10,
                "total_results": 100
            }"""
            )
        mockWebServer.enqueue(mockResponse)

        val response = mediaRemoteDataSource.getDiscoverItems(
            MediaType.MOVIE.path,
            1
        )

        assertTrue(response is ApiResult.Success)
        val discoverResult = (response as ApiResult.Success).data
        assertEquals(discoverResult.page, 1)
        assertEquals(discoverResult.results.size, 0)
        assertEquals(discoverResult.totalPages, 10)
        assertEquals(discoverResult.totalResults, 100)
    }

    @Test
    fun `test error response with correct JSON`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody(
                """{
                "success": false,
                "status_code": 7,
                "status_message": "Invalid API key."
            }"""
            )
        mockWebServer.enqueue(mockResponse)

        val response = mediaRemoteDataSource.getDiscoverItems(
            MediaType.MOVIE.path,
            1,
        )

        assertTrue(response is ApiResult.Error)
        val apiError = (response as ApiResult.Error).apiError
        assertTrue(apiError is ApiError.InvalidApiKey)
    }

    @Test
    fun `test HTTP status error with no body`() {
        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        val response = runBlocking {
            mediaRemoteDataSource.getDiscoverItems(
                MediaType.MOVIE.path,
                1
            )
        }
        val apiError = (response as ApiResult.Error).apiError
        assertTrue(apiError is ApiError.InternalError)
    }

    @Test
    fun `test broken JSON response`() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{ broken_json")
        mockWebServer.enqueue(mockResponse)

        val response = runBlocking {
            mediaRemoteDataSource.getDiscoverItems(
                MediaType.MOVIE.path,
                1
            )
        }
        val apiError = (response as ApiResult.Error).apiError
        assertTrue(apiError is ApiError.Unknown)
    }

    @Test
    fun `test unexpected HTML response`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("<html><body>Error</body></html>")
        mockWebServer.enqueue(mockResponse)

        val response = mediaRemoteDataSource.getDiscoverItems(
            MediaType.MOVIE.path,
            1
        )
        val apiError = (response as ApiResult.Error).apiError
        assertTrue(apiError is ApiError.Unknown)
    }
}