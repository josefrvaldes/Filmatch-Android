package es.josevaldes.data

import es.josevaldes.core.utils.md5
import es.josevaldes.data.model.ContentType
import es.josevaldes.data.model.Duration
import es.josevaldes.data.model.GenreData
import es.josevaldes.data.model.MediaFilters
import es.josevaldes.data.model.Provider
import es.josevaldes.data.model.Score
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.time.LocalDateTime

class MediaFiltersTest {

    private val testProvider1 = Provider(1, "Provider 1", "path_to.jpg", 1, mapOf())
    private val testProvider2 = Provider(2, "Provider 2", "path_to.jpg", 1, mapOf())
    private val testProvider3 = Provider(3, "Provider 3", "path_to.jpg", 1, mapOf())
    private val testProvider4 = Provider(4, "Provider 4", "path_to.jpg", 1, mapOf())

    @Test
    fun `filtersHash should be consistent for the same genres in different orders`() {
        val genres1 = listOf(GenreData(1, "1"), GenreData(2, "2"))
        val genres2 = listOf(GenreData(2, "2"), GenreData(1, "1"))

        val filters1 = MediaFilters(genres = genres1)
        val filters2 = MediaFilters(genres = genres2)

        assertEquals(filters1.filtersHash, filters2.filtersHash)
    }


    @Test
    fun `filtersHash should be consistent for the same providers in different orders`() {
        val providers1 = listOf(testProvider1, testProvider2)
        val providers2 = listOf(testProvider2, testProvider1)

        val filters1 = MediaFilters(providers = providers1)
        val filters2 = MediaFilters(providers = providers2)

        assertEquals(filters1.filtersHash, filters2.filtersHash)
    }


    @Test
    fun `filtersHash should change when content type changes`() {
        val filtersMovies = MediaFilters(contentType = ContentType.MOVIES)
        val filtersTv = MediaFilters(contentType = ContentType.TV_SHOWS)

        assertNotEquals(filtersMovies.filtersHash, filtersTv.filtersHash)
    }

    @Test
    fun `filtersHash should include all filter parameters`() {
        val filters = MediaFilters(
            contentType = ContentType.TV_SHOWS,
            genres = listOf(GenreData(1, "1"), GenreData(2, "2")),
            providers = listOf(testProvider1),
            duration = Duration(120),
            score = Score(8.0f),
            yearFrom = 1990,
            yearTo = 2020,
            sortBy = "release_date.desc"
        )

        val expectedHashString = "tv-1|2-1-120-8.0-1990-2020-release_date.desc"
        assertEquals(expectedHashString.md5(), filters.filtersHash)
    }

    @Test
    fun `filtersHash should be consistent with default parameters`() {
        val filtersDefault = MediaFilters()
        val filtersExplicit = MediaFilters(
            contentType = ContentType.MOVIES,
            genres = listOf(),
            providers = listOf(),
            duration = null,
            score = null,
            yearFrom = 2000,
            yearTo = LocalDateTime.now().year,
            sortBy = "popularity.desc"
        )

        assertEquals(filtersDefault.filtersHash, filtersExplicit.filtersHash)
    }


    @Test
    fun `filtersHash should handle all nullable fields correctly`() {
        val filters = MediaFilters(
            contentType = ContentType.MOVIES,
            genres = null,
            providers = null,
            duration = null,
            score = null,
            yearFrom = null,
            yearTo = null,
            sortBy = "popularity.desc"
        )

        val expectedHashString = "movie-null-null-null-null-null-null-popularity.desc"
        assertEquals(expectedHashString.md5(), filters.filtersHash)
    }


    @Test
    fun `filtersHash should handle a mix of null and non-null fields - combination 1`() {
        val filters = MediaFilters(
            contentType = ContentType.TV_SHOWS,
            genres = listOf(GenreData(1, "1"), GenreData(3, "3"), GenreData(2, "2")),
            providers = null,
            duration = null,
            score = Score(7.5f),
            yearFrom = 2015,
            yearTo = null,
            sortBy = "release_date.asc"
        )

        val expectedHashString = "tv-1|2|3-null-null-7.5-2015-null-release_date.asc"
        assertEquals(expectedHashString.md5(), filters.filtersHash)
    }

    @Test
    fun `filtersHash should handle a mix of null and non-null fields - combination 2`() {
        val filters = MediaFilters(
            contentType = ContentType.MOVIES,
            genres = null,
            providers = listOf(testProvider4, testProvider2, testProvider3),
            duration = Duration(120),
            score = null,
            yearFrom = null,
            yearTo = 2022,
            sortBy = "popularity.desc"
        )

        val expectedHashString = "movie-null-2|3|4-120-null-null-2022-popularity.desc"
        assertEquals(expectedHashString.md5(), filters.filtersHash)
    }

    @Test
    fun `filtersHash should handle a mix of null and non-null fields - combination 3`() {
        val filters = MediaFilters(
            contentType = ContentType.TV_SHOWS,
            genres = null,
            providers = null,
            duration = null,
            score = null,
            yearFrom = 2000,
            yearTo = 2020,
            sortBy = "popularity.desc"
        )

        val expectedHashString = "tv-null-null-null-null-2000-2020-popularity.desc"
        val receivedMd5Hash = filters.filtersHash
        assertEquals(expectedHashString.md5(), filters.filtersHash)
        println("hash string=$expectedHashString, md5=$receivedMd5Hash")
    }

    @Test
    fun `filtersHash should handle a mix of null and non-null fields - combination 4`() {
        val filters = MediaFilters(
            contentType = ContentType.MOVIES,
            genres = null,
            providers = null,
            duration = Duration(120),
            score = null,
            yearFrom = 2005,
            yearTo = 2025,
            sortBy = "popularity.desc"
        )

        val expectedHashString = "movie-null-null-120-null-2005-2025-popularity.desc"
        val receivedHashString = filters.getFiltersHashString()
        val receivedMd5Hash = receivedHashString.md5()
        assertEquals(expectedHashString.md5(), filters.filtersHash)
        println("hash string=$expectedHashString, md5=$receivedMd5Hash")
    }

    @Test
    fun `filtersHash should handle a mix of null and non-null fields - combination 5`() {
        val filters = MediaFilters(
            contentType = ContentType.MOVIES,
            genres = listOf(GenreData(3, "3"), GenreData(1, "1")),
            providers = null,
            duration = null,
            score = null,
            yearFrom = 2005,
            yearTo = 2025,
            sortBy = "popularity.desc"
        )

        val expectedHashString = "movie-1|3-null-null-null-2005-2025-popularity.desc"
        val receivedHashString = filters.getFiltersHashString()
        val reveicedMd5Hash = receivedHashString.md5()
        assertEquals(expectedHashString.md5(), filters.filtersHash)
        println("hash string=$expectedHashString, md5=$reveicedMd5Hash")
    }

    @Test
    fun `filtersHash should handle a mix of null and non-null fields - combination 6`() {
        val filters = MediaFilters(
            contentType = ContentType.MOVIES,
            genres = listOf(GenreData(3, "3"), GenreData(1, "1")),
            providers = listOf(testProvider3, testProvider1),
            duration = Duration(120),
            score = Score(5.0f),
            yearFrom = 2005,
            yearTo = 2025,
            sortBy = "popularity.desc"
        )

        val expectedHashString = "movie-1|3-1|3-120-5.0-2005-2025-popularity.desc"
        val receivedHashString = filters.getFiltersHashString()
        val receivedMd5Hash = receivedHashString.md5()
        assertEquals(expectedHashString.md5(), filters.filtersHash)
        println("hash string=$expectedHashString, md5=$receivedMd5Hash")
    }
}