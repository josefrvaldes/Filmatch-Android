package es.josevaldes.data

import es.josevaldes.data.model.CreditsData
import es.josevaldes.data.model.CrewMemberData
import es.josevaldes.data.model.DetailsMovieData
import es.josevaldes.data.model.DetailsTvData
import es.josevaldes.data.model.EpisodeData
import es.josevaldes.data.model.GenreData
import org.junit.Assert.assertEquals
import org.junit.Test

class DetailMovieResponseTest {

    @Test
    fun `getCategoriesString should return the right string`() {
        val movie = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            baseGenres = listOf(
                GenreData(id = 1, name = "Action"),
            )
        )
        assertEquals("Action", movie.getGenresString())

        val movie2 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            baseGenres = listOf(
                GenreData(id = 1, name = "Action"),
                GenreData(id = 2, name = "Adventure"),
            )
        )
        assertEquals("Action and Adventure", movie2.getGenresString())

        val movie3 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            baseGenres = listOf(
                GenreData(id = 1, name = "Action"),
                GenreData(id = 2, name = "Adventure"),
                GenreData(id = 3, name = "Comedy"),
            )
        )
        assertEquals("Action, Adventure and Comedy", movie3.getGenresString())

        val movie4 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            baseGenres = listOf(
                GenreData(id = 1, name = "Action"),
                GenreData(id = 2, name = "Adventure"),
                GenreData(id = 3, name = "Comedy"),
                GenreData(id = 4, name = "Drama"),
            )
        )
        assertEquals("Action, Adventure, Comedy and Drama", movie4.getGenresString())

        val noCategoriesMovie = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            baseGenres = emptyList()
        )
        assertEquals("", noCategoriesMovie.getGenresString())
    }


    @Test
    fun `displayableRuntime returns runtime for movie`() {
        val movie = DetailsMovieData(
            runtime = 120
        )
        assertEquals(120, movie.displayableRuntime())
    }

    @Test
    fun `displayableRuntime returns 0 for movie with null runtime`() {
        val movie = DetailsMovieData(
            runtime = null
        )
        assertEquals(0, movie.displayableRuntime())
    }

    @Test
    fun `displayableRuntime returns first episode runtime for tv show`() {
        val tvShow = DetailsTvData(
            episodeRunTime = listOf(45)
        )
        assertEquals(45, tvShow.displayableRuntime())
    }

    @Test
    fun `displayableRuntime calculates average runtime for tv show`() {
        val tvShow = DetailsTvData(
            nextEpisodeToAir = EpisodeData(id = 0, runtime = 50),
            lastEpisodeToAir = EpisodeData(id = 0, runtime = 40)
        )
        assertEquals(45, tvShow.displayableRuntime())
    }

    @Test
    fun `displayableRuntime returns runtime of next episode if only next episode exists`() {
        val tvShow = DetailsTvData(
            nextEpisodeToAir = EpisodeData(id = 0, runtime = 60),
            lastEpisodeToAir = null
        )
        assertEquals(60, tvShow.displayableRuntime())
    }

    @Test
    fun `displayableRuntime returns runtime of last episode if only last episode exists`() {
        val tvShow = DetailsTvData(
            nextEpisodeToAir = null,
            lastEpisodeToAir = EpisodeData(id = 0, runtime = 50)
        )
        assertEquals(50, tvShow.displayableRuntime())
    }

    @Test
    fun `displayableRuntime returns 0 for tv show with no runtimes`() {
        val tvShow = DetailsTvData(
            episodeRunTime = emptyList(),
            nextEpisodeToAir = null,
            lastEpisodeToAir = null
        )
        assertEquals(0, tvShow.displayableRuntime())
    }


    @Test
    fun `hasRuntime returns true if displayableRuntime is greater than 0 for movie`() {
        val movie = DetailsMovieData(
            runtime = 120
        )
        assertEquals(true, movie.hasRuntime())
    }

    @Test
    fun `hasRuntime returns false if displayableRuntime is 0 for movie`() {
        val movie = DetailsMovieData(
            runtime = null
        )
        assertEquals(false, movie.hasRuntime())
    }

    @Test
    fun `hasRuntime returns true if displayableRuntime is greater than 0 for tv show`() {
        val tvShow = DetailsTvData(
            episodeRunTime = listOf(30)
        )
        assertEquals(true, tvShow.hasRuntime())
    }

    @Test
    fun `hasRuntime returns false if displayableRuntime is 0 for tv show`() {
        val tvShow = DetailsTvData(
            episodeRunTime = emptyList(),
            nextEpisodeToAir = null,
            lastEpisodeToAir = null
        )
        assertEquals(false, tvShow.hasRuntime())
    }

    @Test
    fun `getReleaseYear should return the right year`() {
        val movie = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            releaseDate = "2021-01-01"
        )
        assertEquals("2021", movie.getReleaseYear())

        val movie2 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            releaseDate = "2021-12-31"
        )
        assertEquals("2021", movie2.getReleaseYear())

        val movie3 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            releaseDate = "2021-31-12"
        )
        assertEquals("2021", movie3.getReleaseYear())

        val movie4 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie4.getReleaseYear())

        val movie5 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie5.getReleaseYear())

        val movie6 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie6.getReleaseYear())

        val movie7 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie7.getReleaseYear())

        val movie8 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie8.getReleaseYear())

        val movie9 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie9.getReleaseYear())
    }

    @Test
    fun `getDirectorsString should return the right string`() {
        val movie = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            baseCredits = CreditsData(
                crew = listOf(
                    CrewMemberData(id = 1, name = "Director 1", department = "Directing"),
                )
            )
        )
        assertEquals("Director 1", movie.getDirectorsString("and"))

        val movie2 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            baseCredits = CreditsData(
                crew = listOf(
                    CrewMemberData(id = 1, name = "Director 1", department = "Directing"),
                    CrewMemberData(id = 1, name = "Director 2", department = "Directing"),
                )
            )
        )
        assertEquals("Director 1 and Director 2", movie2.getDirectorsString("and"))

        val movie3 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            baseCredits = CreditsData(
                crew = listOf(
                    CrewMemberData(id = 1, name = "Director 1", department = "Directing"),
                    CrewMemberData(id = 1, name = "Director 2", department = "Directing"),
                    CrewMemberData(id = 1, name = "Director 3", department = "Directing"),
                )
            )
        )
        assertEquals("Director 1, Director 2 and Director 3", movie3.getDirectorsString("and"))

        val movie4 = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            baseCredits = CreditsData(
                crew = listOf(
                    CrewMemberData(id = 1, name = "Director 1", department = "Directing"),
                    CrewMemberData(id = 1, name = "Director 2", department = "Directing"),
                    CrewMemberData(id = 1, name = "Director 3", department = "Directing"),
                    CrewMemberData(id = 1, name = "Director 4", department = "Directing"),
                )
            )
        )
        assertEquals(
            "Director 1, Director 2, Director 3 and Director 4",
            movie4.getDirectorsString("and")
        )

        val noDirectorsMovie = DetailsMovieData(
            baseId = 1,
            title = "Movie 1",
            baseCredits = CreditsData(
                crew = emptyList()
            )
        )
        assertEquals("", noDirectorsMovie.getDirectorsString("and"))
    }
}