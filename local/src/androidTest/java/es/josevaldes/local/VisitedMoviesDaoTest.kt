package es.josevaldes.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.josevaldes.local.dao.LocalDatabase
import es.josevaldes.local.dao.VisitedMoviesDao
import es.josevaldes.local.entities.VisitedMovieEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
}