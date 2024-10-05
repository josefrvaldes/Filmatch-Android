package es.josevaldes.data

import androidx.paging.PagingSource
import es.josevaldes.data.model.Movie
import es.josevaldes.data.paging.MovieDBPagingConfig
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.repositories.MovieRepository
import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiErrorException
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.MovieService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test


class MoviesPagingSourceUnitTest {

    private val movieService: MovieService = mockk()


    @Test
    fun `MoviesPagingSource should load data successfully`() = runBlocking {
        val mockedList1 = mutableListOf<Movie>()
        for (i in 1..20) {
            mockedList1.add(Movie(id = i, title = "Movie $i"))
        }

        val mockedList2 = mutableListOf<Movie>()
        for (i in 21..40) {
            mockedList2.add(Movie(id = i, title = "Movie $i"))
        }

        val mockedList3 = mutableListOf<Movie>()
        for (i in 41..50) {
            mockedList3.add(Movie(id = i, title = "Movie $i"))
        }


        // let's mock the response for the first page
        val mockedResponse1 = DiscoverMoviesResponse(
            results = mockedList1,
            page = 1,
            totalResults = 50,
            totalPages = 3
        )

        // let's mock the response for the second page
        val mockedResponse2 = DiscoverMoviesResponse(
            results = mockedList2,
            page = 2,
            totalResults = 50,
            totalPages = 3
        )

        // let's mock the response for the third page
        val mockedResponse3 = DiscoverMoviesResponse(
            results = mockedList3,
            page = 3,
            totalResults = 50,
            totalPages = 3
        )

        // let's mock a successful response for each page
        coEvery { movieService.getDiscoverMovies(1, any(), any()) } returns ApiResult.Success(
            mockedResponse1
        )
        coEvery { movieService.getDiscoverMovies(2, any(), any()) } returns ApiResult.Success(
            mockedResponse2
        )
        coEvery { movieService.getDiscoverMovies(3, any(), any()) } returns ApiResult.Success(
            mockedResponse3
        )

        // let's init the paging source
        val movieRepository = MovieRepository(movieService)
        val pagingSource = MoviesPagingSource(movieRepository, "en")
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
        assertEquals("Movie 1", page1.data[0].title)
        assertEquals("Movie 20", page1.data[19].title)


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
        assertEquals("Movie 21", page2.data[0].title)
        assertEquals("Movie 40", page2.data[19].title)


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
        assertEquals("Movie 41", page3.data[0].title)
        assertEquals("Movie 50", page3.data[9].title)

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
            movieService.getDiscoverMovies(
                1,
                any(),
                any()
            )
        } returns ApiResult.Error(ApiError.Unknown)

        val movieRepository = MovieRepository(movieService)
        val pagingSource = MoviesPagingSource(movieRepository, "en")

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
            movieService.getDiscoverMovies(
                1,
                any(),
                any()
            )
        } returns ApiResult.Error(ApiError.ResourceNotFound)

        val movieRepository = MovieRepository(movieService)
        val pagingSource = MoviesPagingSource(movieRepository, "en")

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
        val emptyResponse = DiscoverMoviesResponse(
            results = emptyList(),
            page = 1,
            totalResults = 0,
            totalPages = 1
        )

        coEvery { movieService.getDiscoverMovies(1, any(), any()) } returns ApiResult.Success(
            emptyResponse
        )

        val movieRepository = MovieRepository(movieService)
        val pagingSource = MoviesPagingSource(movieRepository, "en")

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
        val fakeMovies = mutableListOf<Movie>()
        for (i in 1..20) {
            fakeMovies.add(Movie(id = i, title = "Movie $i"))
        }

        val firstResponse = DiscoverMoviesResponse(
            results = fakeMovies,
            page = 1,
            totalResults = 50,
            totalPages = 3
        )

        coEvery { movieService.getDiscoverMovies(1, any(), any()) } returns ApiResult.Success(
            firstResponse
        )

        val movieRepository = MovieRepository(movieService)
        val pagingSource = MoviesPagingSource(movieRepository, "en")

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