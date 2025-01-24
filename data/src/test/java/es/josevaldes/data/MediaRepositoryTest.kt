package es.josevaldes.data

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.paging.MediaPagingSource
import es.josevaldes.data.repositories.MediaRepository
import es.josevaldes.data.responses.DetailsMovieResponse
import es.josevaldes.data.responses.DiscoverItem
import es.josevaldes.data.responses.DiscoverMovie
import es.josevaldes.data.responses.DiscoverResponse
import es.josevaldes.data.responses.MediaType
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiErrorException
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.FilmatchRemoteDataSource
import es.josevaldes.data.services.MediaRemoteDataSource
import es.josevaldes.local.datasources.MediaLocalDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MediaRepositoryTest {

    private lateinit var mediaRepository: MediaRepository
    private lateinit var mediaPagingSource: MediaPagingSource
    private lateinit var mediaRemoteDataSource: MediaRemoteDataSource
    private lateinit var mediaLocalDataSource: MediaLocalDataSource
    private lateinit var filmatchRemoteDataSource: FilmatchRemoteDataSource
    private var listOfMovies = mutableListOf<DiscoverItem>()
    private val config = PagingConfig(pageSize = 5, enablePlaceholders = false)

    @Before
    fun setUp() {
        mediaRemoteDataSource = mockk()
        mediaLocalDataSource = mockk()
        filmatchRemoteDataSource = mockk()
        mediaPagingSource = MediaPagingSource(mediaRemoteDataSource, "en")
        mediaRepository =
            MediaRepository(
                mediaPagingSource,
                mediaRemoteDataSource,
                mediaLocalDataSource,
                filmatchRemoteDataSource
            )
        for (i in 1..20) {
            listOfMovies.add(
                DiscoverMovie(id = i, title = "Movie $i")
            )
        }
    }

    @Test
    fun `getDiscoverMovies should return success on valid result`() = runTest {
        val resultList = listOfMovies.subList(0, config.pageSize)
        coEvery {
            mediaRemoteDataSource.getDiscoverItems(
                any(),
                any(),
                any()
            )
        } returns ApiResult.Success(
            DiscoverResponse(
                results = resultList,
                page = 1,
                totalResults = listOfMovies.size,
                totalPages = listOfMovies.size / config.pageSize
            )
        )
        val testPager = TestPager(config, mediaPagingSource)
        val refreshResult = testPager.refresh()
        assertTrue(refreshResult is LoadResult.Page)
        val result = refreshResult as LoadResult.Page<Int, DiscoverMovie>
        assertEquals(resultList.map { it.toAppModel() }, result.data)
    }

    @Test
    fun `getDiscoverMovies should return success after appending data`() = runTest {
        var counter = 0
        var currentSubList = mutableListOf<DiscoverItem>()
        coEvery { mediaRemoteDataSource.getDiscoverItems(any(), any(), any()) } answers {
            currentSubList = listOfMovies.subList(
                counter * config.pageSize,
                counter * config.pageSize + config.pageSize
            )
            counter++
            ApiResult.Success(
                DiscoverResponse(
                    results = currentSubList,
                    page = counter,
                    totalResults = listOfMovies.size,
                    totalPages = listOfMovies.size / config.pageSize
                )
            )
        }
        val pager = TestPager(config, mediaPagingSource)
        val result = with(pager) {
            this.refresh()
            append()
            append()
        } as LoadResult.Page<Int, DiscoverMovie>
        assertEquals(currentSubList.map { it.toAppModel() }, result.data)
    }


    @Test
    fun `getDiscoverMovies should handle Unknown errors correctly`() = runTest {
        coEvery {
            mediaRemoteDataSource.getDiscoverItems(
                any(),
                any(),
                any()
            )
        } returns ApiResult.Error(
            ApiError.Unknown
        )
        val testPager = TestPager(config, mediaPagingSource)
        val result = testPager.refresh() as LoadResult.Error<Int, DiscoverMovie>
        assertEquals(ApiErrorException(ApiError.Unknown).toString(), result.throwable.toString())
    }


    @Test
    fun `getDiscoverMovies should handle ResourceNotFound errors correctly`() = runTest {
        coEvery {
            mediaRemoteDataSource.getDiscoverItems(
                any(),
                any(),
                any()
            )
        } returns ApiResult.Error(
            ApiError.ResourceNotFound
        )
        val testPager = TestPager(config, mediaPagingSource)
        val result = testPager.refresh() as LoadResult.Error<Int, DiscoverMovie>
        assertEquals(
            ApiErrorException(ApiError.ResourceNotFound).toString(),
            result.throwable.toString()
        )
    }

    @Test
    fun `findById should return success on valid result`() = runTest {
        val movie = DetailsMovieResponse(id = 1, title = "hello")
        coEvery { mediaRemoteDataSource.findById(any(), any(), any()) } returns ApiResult.Success(
            movie
        )
        val resultFlow = mediaRepository.findById(movie.id, MediaType.MOVIE, "es-ES")
        resultFlow.collect {
            assertEquals(ApiResult.Success(movie.toAppModel()), it)
        }
    }

    @Test
    fun `findById should return error on invalid result`() = runTest {
        coEvery {
            mediaRemoteDataSource.findById(
                any(),
                any(),
                any()
            )
        } returns ApiResult.Error(ApiError.Unknown)
        val resultFlow = mediaRepository.findById(1, MediaType.MOVIE, "es-ES")
        resultFlow.collect {
            assertEquals(ApiResult.Error(ApiError.Unknown), it)
        }
    }

    @Test
    fun `findById should return error on resource not found`() = runTest {
        coEvery {
            mediaRemoteDataSource.findById(
                any(),
                any(),
                any()
            )
        } returns ApiResult.Error(ApiError.ResourceNotFound)
        val resultFlow = mediaRepository.findById(1, MediaType.MOVIE, "es-ES")
        resultFlow.collect {
            assertEquals(ApiResult.Error(ApiError.ResourceNotFound), it)
        }
    }
}