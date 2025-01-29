package es.josevaldes.data

import androidx.paging.PagingSource
import es.josevaldes.data.model.DiscoverMovieData
import es.josevaldes.data.paging.MediaPagingSource
import es.josevaldes.data.paging.MovieDBPagingConfig
import es.josevaldes.data.responses.DiscoverItem
import es.josevaldes.data.responses.DiscoverMovie
import es.josevaldes.data.responses.DiscoverResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiErrorException
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MediaRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test


class MediaPagingSourceUnitTest {

    private val mediaRemoteDataSource: MediaRemoteDataSource = mockk()


    @Test
    fun `MoviesPagingSource should load data successfully`() = runBlocking {
        val mockedList1 = mutableListOf<DiscoverItem>()
        for (i in 1..20) {
            mockedList1.add(DiscoverMovie(id = i, title = "Movie $i"))
        }

        val mockedList2 = mutableListOf<DiscoverItem>()
        for (i in 21..40) {
            mockedList2.add(DiscoverMovie(id = i, title = "Movie $i"))
        }

        val mockedList3 = mutableListOf<DiscoverItem>()
        for (i in 41..50) {
            mockedList3.add(DiscoverMovie(id = i, title = "Movie $i"))
        }


        // let's mock the response for the first page
        val mockedResponse1 = DiscoverResponse(
            results = mockedList1,
            page = 1,
            totalResults = 50,
            totalPages = 3
        )

        // let's mock the response for the second page
        val mockedResponse2 = DiscoverResponse(
            results = mockedList2,
            page = 2,
            totalResults = 50,
            totalPages = 3
        )

        // let's mock the response for the third page
        val mockedResponse3 = DiscoverResponse(
            results = mockedList3,
            page = 3,
            totalResults = 50,
            totalPages = 3
        )

        // let's mock a successful response for each page
        coEvery {
            mediaRemoteDataSource.getDiscoverItems(
                "movie",
                1,
                any(),
                any()
            )
        } returns ApiResult.Success(
            mockedResponse1
        )
        coEvery {
            mediaRemoteDataSource.getDiscoverItems(
                "movie",
                2,
                any(),
                any()
            )
        } returns ApiResult.Success(
            mockedResponse2
        )
        coEvery {
            mediaRemoteDataSource.getDiscoverItems(
                "movie",
                3,
                any(),
                any()
            )
        } returns ApiResult.Success(
            mockedResponse3
        )

        // let's init the paging source
        val pagingSource = MediaPagingSource(fetchMovies = { page ->
            mediaRemoteDataSource.getDiscoverItems("movie", page, "en")
        })
        val pageSize = MovieDBPagingConfig.pagingConfig.pageSize
        val enablePlaceholders = MovieDBPagingConfig.pagingConfig.enablePlaceholders

        // 1. let's load the first page
        val resultPage1 = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null, // Key null means that we are loading the first page
                loadSize = pageSize,
                placeholdersEnabled = enablePlaceholders
            )
        )
        // Let's verify that we have the proper data
        assert(resultPage1 is PagingSource.LoadResult.Page)
        val page1 = resultPage1 as PagingSource.LoadResult.Page
        assertEquals(20, page1.data.size)
        assertEquals("Movie 1", (page1.data[0] as DiscoverMovieData).title)
        assertEquals("Movie 20", (page1.data[19] as DiscoverMovieData).title)


        // 2. let's load the second page
        val resultPage2 = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 2,       // let's indicate that we are loading the second page
                loadSize = pageSize,
                placeholdersEnabled = enablePlaceholders
            )
        )

        // let's verify that the second page returns the correct results
        assert(resultPage2 is PagingSource.LoadResult.Page)
        val page2 = resultPage2 as PagingSource.LoadResult.Page
        assertEquals(20, page2.data.size)
        assertEquals("Movie 21", (page2.data[0] as DiscoverMovieData).title)
        assertEquals("Movie 40", (page2.data[19] as DiscoverMovieData).title)


        // 3. let's load the third page
        val resultPage3 = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 3,       // we want page 3
                loadSize = 10, // last page only has 10 items
                placeholdersEnabled = enablePlaceholders
            )
        )

        // let's verify that the third page returns the correct results
        assert(resultPage3 is PagingSource.LoadResult.Page)
        val page3 = resultPage3 as PagingSource.LoadResult.Page
        assertEquals(10, page3.data.size)
        assertEquals("Movie 41", (page3.data[0] as DiscoverMovieData).title)
        assertEquals("Movie 50", (page3.data[9] as DiscoverMovieData).title)

        assertEquals(null, page1.prevKey) // there shouldn't be a previous page
        assertEquals(2, page1.nextKey)    // next page should be 2

        assertEquals(1, page2.prevKey)    // previous page should be 1
        assertEquals(3, page2.nextKey)    // next page should be 3

        assertEquals(2, page3.prevKey)    // previous page should be 2
        assertEquals(null, page3.nextKey) // there shouldn't be a next page
    }

    @Test
    fun `MoviesPagingSource should handle server exception properly`() = runBlocking {
        coEvery {
            mediaRemoteDataSource.getDiscoverItems(
                "movie",
                1,
                any(),
                any()
            )
        } returns ApiResult.Error(ApiError.Unknown)

        val pagingSource = MediaPagingSource(fetchMovies = { page ->
            mediaRemoteDataSource.getDiscoverItems("movie", page, "en")
        })

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = MovieDBPagingConfig.pagingConfig.pageSize,
                placeholdersEnabled = MovieDBPagingConfig.pagingConfig.enablePlaceholders
            )
        )

        assert(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assert(error.throwable is ApiErrorException)
        assertEquals(ApiError.Unknown, (error.throwable as ApiErrorException).apiError)
    }

    @Test
    fun `MoviesPagingSource should handle 404 error properly`() = runBlocking {
        coEvery {
            mediaRemoteDataSource.getDiscoverItems(
                "movie",
                1,
                any(),
                any()
            )
        } returns ApiResult.Error(ApiError.ResourceNotFound)

        val pagingSource = MediaPagingSource(fetchMovies = { page ->
            mediaRemoteDataSource.getDiscoverItems("movie", page, "en")
        })

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = MovieDBPagingConfig.pagingConfig.pageSize,
                placeholdersEnabled = MovieDBPagingConfig.pagingConfig.enablePlaceholders
            )
        )

        assert(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assert(error.throwable is ApiErrorException)

        val throwableError = error.throwable as ApiErrorException
        assertEquals(ApiError.ResourceNotFound, throwableError.apiError)
    }

    @Test
    fun `MoviesPagingSource should handle empty response properly`() = runBlocking {
        val emptyResponse = DiscoverResponse(
            results = emptyList(),
            page = 1,
            totalResults = 0,
            totalPages = 1
        )

        coEvery {
            mediaRemoteDataSource.getDiscoverItems(
                "movie",
                1,
                any(),
                any()
            )
        } returns ApiResult.Success(
            emptyResponse
        )

        val pagingSource = MediaPagingSource(fetchMovies = { page ->
            mediaRemoteDataSource.getDiscoverItems("movie", page, "en")
        })

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = MovieDBPagingConfig.pagingConfig.pageSize,
                placeholdersEnabled = MovieDBPagingConfig.pagingConfig.enablePlaceholders
            )
        )

        assert(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(0, page.data.size)
    }


    @Test
    fun `MoviesPagingSource should fetching invalid pages correctly`() = runBlocking {
        val fakeMovies = mutableListOf<DiscoverItem>()
        for (i in 1..20) {
            fakeMovies.add(DiscoverMovie(id = i, title = "Movie $i"))
        }

        val firstResponse = DiscoverResponse(
            results = fakeMovies,
            page = 1,
            totalResults = 50,
            totalPages = 3
        )

        coEvery {
            mediaRemoteDataSource.getDiscoverItems(
                "movie",
                1,
                any(),
                any()
            )
        } returns ApiResult.Success(
            firstResponse
        )

        val pagingSource = MediaPagingSource(fetchMovies = { page ->
            mediaRemoteDataSource.getDiscoverItems("movie", page, "en")
        })

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = MovieDBPagingConfig.pagingConfig.pageSize,
                placeholdersEnabled = MovieDBPagingConfig.pagingConfig.enablePlaceholders
            )
        )

        assert(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(20, page.data.size)
    }
}