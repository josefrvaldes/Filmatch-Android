package es.josevaldes.data

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import es.josevaldes.data.extensions.mappers.toAppModel
import es.josevaldes.data.extensions.mappers.toLocalModel
import es.josevaldes.data.model.DiscoverMovieData
import es.josevaldes.data.model.DiscoverTvData
import es.josevaldes.data.model.InterestStatus
import es.josevaldes.data.paging.MediaPagingSource
import es.josevaldes.data.repositories.MediaRepository
import es.josevaldes.data.responses.DetailsMovieResponse
import es.josevaldes.data.responses.DiscoverItem
import es.josevaldes.data.responses.DiscoverMovie
import es.josevaldes.data.responses.DiscoverResponse
import es.josevaldes.data.responses.GetVisitStatusResponse
import es.josevaldes.data.responses.GetVisitsByIdsResponse
import es.josevaldes.data.responses.MediaType
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiErrorException
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.FilmatchRemoteDataSource
import es.josevaldes.data.services.MediaRemoteDataSource
import es.josevaldes.local.datasources.MediaLocalDataSource
import es.josevaldes.local.entities.MediaEntityType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
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
    fun `getVisitsByIds should return success with visited ids`() = runTest {
        // Datos de prueba
        val medias = listOf(
            DiscoverMovieData(id = 101, title = "Movie 1"),
            DiscoverMovieData(id = 102, title = "Movie 2"),
            DiscoverMovieData(id = 103, title = "Movie 3")
        )
        val expectedVisitedIds = listOf(101, 103)


        coEvery { filmatchRemoteDataSource.getMovieVisitsByIds(any()) } returns ApiResult.Success(
            GetVisitsByIdsResponse(visited = expectedVisitedIds)
        )

        val result = mediaRepository.getVisitsByIds(medias).first()

        assertTrue(result is ApiResult.Success)
        assertEquals(expectedVisitedIds, (result as ApiResult.Success).data)
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
    fun `getMediaVisitStatus should return status from local data source`() = runTest {
        val media = DiscoverMovieData(id = 101, title = "Movie 1")
        val expectedStatus = InterestStatus.INTERESTED

        coEvery {
            mediaLocalDataSource.getMediaStatus(
                101,
                MediaEntityType.MOVIE
            )
        } returns expectedStatus.toLocalModel()


        val result = mediaRepository.getMediaVisitStatus(media)


        assertEquals(expectedStatus, result)
        coVerify(exactly = 0) { filmatchRemoteDataSource.getMovieVisitStatus(any()) }
    }


    @Test
    fun `getMediaVisitStatus should fetch from remote when not in local`() = runTest {
        val media = DiscoverTvData(id = 202, name = "TV Show 1")
        val expectedStatus = InterestStatus.NOT_INTERESTED

        coEvery { mediaLocalDataSource.getMediaStatus(202, MediaEntityType.TV) } returns null


        coEvery { filmatchRemoteDataSource.getTvVisitStatus(202) } returns ApiResult.Success(
            GetVisitStatusResponse(status = expectedStatus.ordinal)
        )

        val result = mediaRepository.getMediaVisitStatus(media)

        assertEquals(expectedStatus, result)
        coVerify { filmatchRemoteDataSource.getTvVisitStatus(202) }
    }

    @Test
    fun `getMediaVisitStatus should return null if not found in local or remote`() = runTest {
        val media = DiscoverMovieData(id = 303, title = "Movie 3")

        coEvery { mediaLocalDataSource.getMediaStatus(303, MediaEntityType.MOVIE) } returns null

        coEvery { filmatchRemoteDataSource.getMovieVisitStatus(303) } returns ApiResult.Error(
            ApiError.ResourceNotFound
        )

        val result = mediaRepository.getMediaVisitStatus(media)

        assertNull(result)
    }


    @Test
    fun `isMovieVisited should return true if media has a valid status`() = runTest {
        val media = DiscoverMovieData(id = 101, title = "Movie 1")

        coEvery {
            mediaLocalDataSource.getMediaStatus(
                101,
                MediaEntityType.MOVIE
            )
        } returns InterestStatus.WATCHED.toLocalModel()

        val result = mediaRepository.isMovieVisited(media)

        assertTrue(result)
    }

    @Test
    fun `isMovieVisited should return true if media has remote status`() = runTest {
        val media = DiscoverMovieData(id = 101, title = "Movie 1")

        coEvery { mediaLocalDataSource.getMediaStatus(101, MediaEntityType.MOVIE) } returns null

        coEvery { filmatchRemoteDataSource.getMovieVisitStatus(101) } returns ApiResult.Success(
            GetVisitStatusResponse(status = InterestStatus.INTERESTED.ordinal)
        )

        val result = mediaRepository.isMovieVisited(media)

        assertTrue(result)
    }

    @Test
    fun `isMovieVisited should return false if no status exists locally or remotely`() = runTest {
        val media = DiscoverMovieData(id = 101, title = "Movie 1")


        coEvery { mediaLocalDataSource.getMediaStatus(101, MediaEntityType.MOVIE) } returns null


        coEvery { filmatchRemoteDataSource.getMovieVisitStatus(101) } returns ApiResult.Error(
            ApiError.ResourceNotFound
        )

        val result = mediaRepository.isMovieVisited(media)

        assertFalse(result)
    }


    @Test
    fun `isMovieVisited should return false if remote status fails`() = runTest {
        val media = DiscoverMovieData(id = 101, title = "Movie 1")

        coEvery { mediaLocalDataSource.getMediaStatus(101, MediaEntityType.MOVIE) } returns null

        coEvery { filmatchRemoteDataSource.getMovieVisitStatus(101) } returns ApiResult.Error(
            ApiError.Unknown
        )

        val result = mediaRepository.isMovieVisited(media)

        assertFalse(result)
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