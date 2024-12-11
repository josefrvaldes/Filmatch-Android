package es.josevaldes.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import es.josevaldes.local.dao.LocalDatabase
import es.josevaldes.local.dao.VisitedFiltersDao
import es.josevaldes.local.entities.VisitedFiltersEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class VisitedFiltersDaoTest {

    private lateinit var database: LocalDatabase
    private lateinit var visitedFiltersDao: VisitedFiltersDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalDatabase::class.java
        ).allowMainThreadQueries().build()

        visitedFiltersDao = database.visitedFiltersDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun setupIsCorrect() {
        assertNotNull(database)
        assertNotNull(visitedFiltersDao)
    }

    @Test
    fun insertVisitedFilters_shouldInsertCorrectly() = runBlocking {
        val filters = VisitedFiltersEntity(filtersHash = "testHash", maxPage = 1)

        visitedFiltersDao.insertVisitedFilters(filters)
        val maxPage = visitedFiltersDao.getMaxPage("testHash")

        assertNotNull(maxPage)
        assertEquals(1, maxPage)
    }

    @Test
    fun updateMaxPage_shouldUpdateCorrectly() = runBlocking {
        val filters = VisitedFiltersEntity(filtersHash = "testHash", maxPage = 1)
        visitedFiltersDao.insertVisitedFilters(filters)
        val maxPage = visitedFiltersDao.getMaxPage("testHash")
        assertEquals(1, maxPage)

        visitedFiltersDao.updateMaxPage("testHash", 5)
        val updatedMaxPage = visitedFiltersDao.getMaxPage("testHash")

        assertNotNull(updatedMaxPage)
        assertEquals(5, updatedMaxPage)
    }

    @Test
    fun getMaxPage_whenFiltersNotExist_shouldReturnNull() = runBlocking {
        val maxPage = visitedFiltersDao.getMaxPage("nonExistentHash")
        assertNull(maxPage)
    }
}