package es.josevaldes.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.josevaldes.local.dao.LocalDatabase
import es.josevaldes.local.dao.VisitedMoviesDao
import es.josevaldes.local.entities.VisitedFiltersEntity
import es.josevaldes.local.entities.VisitedMovieEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class VisitedMoviesDaoTest {

    private lateinit var database: LocalDatabase
    private lateinit var visitedMoviesDao: VisitedMoviesDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalDatabase::class.java
        ).allowMainThreadQueries().build()

        visitedMoviesDao = database.visitedMoviesDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun setupIsCorrect() {
        assertNotNull(database)
        assertNotNull(visitedMoviesDao)
    }

    @Test
    fun insertVisitedMovie_shouldInsertCorrectly() = runBlocking {
        val movie = VisitedMovieEntity(id = "movie1")

        visitedMoviesDao.insertVisitedMovie(movie)
        val visitedMovies = visitedMoviesDao.getVisitedMovies()

        assertEquals(1, visitedMovies.size)
        assertEquals("movie1", visitedMovies[0].id)
    }

    @Test
    fun insertVisitedMovies_shouldInsertMultipleCorrectly() = runBlocking {
        val movies = listOf(
            VisitedMovieEntity(id = "movie1"),
            VisitedMovieEntity(id = "movie2"),
            VisitedMovieEntity(id = "movie3")
        )

        visitedMoviesDao.insertVisitedMovies(movies)
        val visitedMovies = visitedMoviesDao.getVisitedMovies()

        assertEquals(3, visitedMovies.size)
        assertEquals("movie1", visitedMovies[0].id)
        assertEquals("movie2", visitedMovies[1].id)
        assertEquals("movie3", visitedMovies[2].id)
    }

    @Test
    fun getVisitedMovies_whenNoMovies_shouldReturnEmptyList() = runBlocking {
        val visitedMovies = visitedMoviesDao.getVisitedMovies()

        assertTrue(visitedMovies.isEmpty())
    }


    @Test
    fun isMovieVisited_whenMovieExists_shouldReturnTrue() = runBlocking {
        val movie = VisitedMovieEntity(id = "1")
        visitedMoviesDao.insertVisitedMovie(movie)

        val result = visitedMoviesDao.isMovieVisited("1")
        assertTrue(result)
    }

    @Test
    fun isMovieVisited_whenMovieDoesNotExist_shouldReturnFalse() = runBlocking {
        val result = visitedMoviesDao.isMovieVisited("1")
        assertFalse(result)
    }

    @Test
    fun isMovieVisited_whenDifferentMovieExists_shouldReturnFalse() = runBlocking {
        val movie = VisitedMovieEntity(id = "2")
        visitedMoviesDao.insertVisitedMovie(movie)

        val result = visitedMoviesDao.isMovieVisited("1")
        assertFalse(result)
    }

    @Test
    fun deleteVisitedMovie_whenMovieExists_shouldRemoveMovie() = runBlocking {
        val movie = VisitedMovieEntity(id = "movie1")
        visitedMoviesDao.insertVisitedMovie(movie)

        var isVisited = visitedMoviesDao.isMovieVisited("movie1")
        assertTrue(isVisited)

        visitedMoviesDao.deleteVisitedMovie("movie1")

        isVisited = visitedMoviesDao.isMovieVisited("movie1")
        assertFalse(isVisited)
    }

    @Test
    fun deleteVisitedMovie_whenMovieDoesNotExist_shouldDoNothing() = runBlocking {
        visitedMoviesDao.deleteVisitedMovie("nonExistentMovie")

        val visitedMovies = visitedMoviesDao.getVisitedMovies()
        assertTrue(visitedMovies.isEmpty())
    }

    @Test
    fun deleteVisitedMovie_whenMultipleMoviesExist_shouldRemoveOnlySpecifiedMovie() = runBlocking {
        val movies = listOf(
            VisitedMovieEntity(id = "movie1"),
            VisitedMovieEntity(id = "movie2"),
            VisitedMovieEntity(id = "movie3")
        )
        visitedMoviesDao.insertVisitedMovies(movies)

        var isVisitedMovie1 = visitedMoviesDao.isMovieVisited("movie1")
        var isVisitedMovie2 = visitedMoviesDao.isMovieVisited("movie2")
        var isVisitedMovie3 = visitedMoviesDao.isMovieVisited("movie3")
        assertTrue(isVisitedMovie1)
        assertTrue(isVisitedMovie2)
        assertTrue(isVisitedMovie3)

        visitedMoviesDao.deleteVisitedMovie("movie2")

        isVisitedMovie1 = visitedMoviesDao.isMovieVisited("movie1")
        isVisitedMovie2 = visitedMoviesDao.isMovieVisited("movie2")
        isVisitedMovie3 = visitedMoviesDao.isMovieVisited("movie3")

        assertTrue(isVisitedMovie1)
        assertFalse(isVisitedMovie2)
        assertTrue(isVisitedMovie3)
    }


    @Test
    fun insertVisitedFilters_shouldInsertCorrectly() = runBlocking {
        val filters = VisitedFiltersEntity(filtersHash = "testHash", maxPage = 1)

        visitedMoviesDao.insertVisitedFilters(filters)
        val maxPage = visitedMoviesDao.getMaxPage("testHash")

        assertNotNull(maxPage)
        assertEquals(1, maxPage)
    }

    @Test
    fun updateMaxPage_shouldUpdateCorrectly() = runBlocking {
        val filters = VisitedFiltersEntity(filtersHash = "testHash", maxPage = 1)
        visitedMoviesDao.insertVisitedFilters(filters)
        val maxPage = visitedMoviesDao.getMaxPage("testHash")
        assertEquals(1, maxPage)

        visitedMoviesDao.updateMaxPage("testHash", 5)
        val updatedMaxPage = visitedMoviesDao.getMaxPage("testHash")

        assertNotNull(updatedMaxPage)
        assertEquals(5, updatedMaxPage)
    }

    @Test
    fun getMaxPage_whenFiltersNotExist_shouldReturnNull() = runBlocking {
        val maxPage = visitedMoviesDao.getMaxPage("nonExistentHash")
        assertNull(maxPage)
    }

    @Test
    fun deleteVisitedFilters_whenFilterExists_shouldRemoveFilter() = runBlocking {
        val filter = VisitedFiltersEntity(filtersHash = "testHash", maxPage = 1)
        visitedMoviesDao.insertVisitedFilters(filter)

        var maxPage = visitedMoviesDao.getMaxPage("testHash")
        assertNotNull(maxPage)
        assertEquals(1, maxPage)

        visitedMoviesDao.deleteVisitedFilters("testHash")

        maxPage = visitedMoviesDao.getMaxPage("testHash")
        assertNull(maxPage)
    }

    @Test
    fun deleteVisitedFilters_whenFilterDoesNotExist_shouldDoNothing() = runBlocking {
        visitedMoviesDao.deleteVisitedFilters("nonExistentHash")

        val maxPage = visitedMoviesDao.getMaxPage("nonExistentHash")
        assertNull(maxPage)
    }

    @Test
    fun deleteVisitedFilters_whenMultipleFiltersExist_shouldRemoveOnlySpecifiedFilter() =
        runBlocking {
            val filters = listOf(
                VisitedFiltersEntity(filtersHash = "hash1", maxPage = 1),
                VisitedFiltersEntity(filtersHash = "hash2", maxPage = 2),
                VisitedFiltersEntity(filtersHash = "hash3", maxPage = 3)
            )
            filters.forEach { visitedMoviesDao.insertVisitedFilters(it) }

            assertEquals(1, visitedMoviesDao.getMaxPage("hash1"))
            assertEquals(2, visitedMoviesDao.getMaxPage("hash2"))
            assertEquals(3, visitedMoviesDao.getMaxPage("hash3"))

            visitedMoviesDao.deleteVisitedFilters("hash2")

            assertEquals(1, visitedMoviesDao.getMaxPage("hash1"))
            assertNull(visitedMoviesDao.getMaxPage("hash2"))
            assertEquals(3, visitedMoviesDao.getMaxPage("hash3"))
        }
}