package es.josevaldes.data

import es.josevaldes.data.model.Credits
import es.josevaldes.data.model.CrewMember
import es.josevaldes.data.model.Genre
import es.josevaldes.data.model.Movie
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieResponseTest {

    @Test
    fun `getCategoriesString should return the right string`() {
        val movie = Movie(
            id = 1,
            title = "Movie 1",
            genres = listOf(
                Genre(id = 1, name = "Action"),
            )
        )
        assertEquals("Action", movie.getGenresString())

        val movie2 = Movie(
            id = 1,
            title = "Movie 1",
            genres = listOf(
                Genre(id = 1, name = "Action"),
                Genre(id = 2, name = "Adventure"),
            )
        )
        assertEquals("Action and Adventure", movie2.getGenresString())

        val movie3 = Movie(
            id = 1,
            title = "Movie 1",
            genres = listOf(
                Genre(id = 1, name = "Action"),
                Genre(id = 2, name = "Adventure"),
                Genre(id = 3, name = "Comedy"),
            )
        )
        assertEquals("Action, Adventure and Comedy", movie3.getGenresString())

        val movie4 = Movie(
            id = 1,
            title = "Movie 1",
            genres = listOf(
                Genre(id = 1, name = "Action"),
                Genre(id = 2, name = "Adventure"),
                Genre(id = 3, name = "Comedy"),
                Genre(id = 4, name = "Drama"),
            )
        )
        assertEquals("Action, Adventure, Comedy and Drama", movie4.getGenresString())

        val noCategoriesMovie = Movie(
            id = 1,
            title = "Movie 1",
            genres = emptyList()
        )
        assertEquals("", noCategoriesMovie.getGenresString())
    }

    @Test
    fun `getDurationString should return the right string`() {
        val movie = Movie(
            id = 1,
            title = "Movie 1",
            runtime = 120
        )
        assertEquals("2h 0m", movie.getDurationString())

        val movie2 = Movie(
            id = 1,
            title = "Movie 1",
            runtime = 150
        )
        assertEquals("2h 30m", movie2.getDurationString())

        val movie3 = Movie(
            id = 1,
            title = "Movie 1",
            runtime = 90
        )
        assertEquals("1h 30m", movie3.getDurationString())

        val movie4 = Movie(
            id = 1,
            title = "Movie 1",
            runtime = 60
        )
        assertEquals("1h 0m", movie4.getDurationString())

        val movie5 = Movie(
            id = 1,
            title = "Movie 1",
            runtime = 30
        )
        assertEquals("0h 30m", movie5.getDurationString())

        val movie6 = Movie(
            id = 1,
            title = "Movie 1",
            runtime = 15
        )
        assertEquals("0h 15m", movie6.getDurationString())

        val movie7 = Movie(
            id = 1,
            title = "Movie 1",
            runtime = 0
        )
        assertEquals("0h 0m", movie7.getDurationString())

        val movie8 = Movie(
            id = 1,
            title = "Movie 1",
            runtime = null
        )
        assertEquals("0h 0m", movie8.getDurationString())
    }

    @Test
    fun `getReleaseYear should return the right year`() {
        val movie = Movie(
            id = 1,
            title = "Movie 1",
            releaseDate = "2021-01-01"
        )
        assertEquals("2021", movie.getReleaseYear())

        val movie2 = Movie(
            id = 1,
            title = "Movie 1",
            releaseDate = "2021-12-31"
        )
        assertEquals("2021", movie2.getReleaseYear())

        val movie3 = Movie(
            id = 1,
            title = "Movie 1",
            releaseDate = "2021-31-12"
        )
        assertEquals("2021", movie3.getReleaseYear())

        val movie4 = Movie(
            id = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie4.getReleaseYear())

        val movie5 = Movie(
            id = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie5.getReleaseYear())

        val movie6 = Movie(
            id = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie6.getReleaseYear())

        val movie7 = Movie(
            id = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie7.getReleaseYear())

        val movie8 = Movie(
            id = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie8.getReleaseYear())

        val movie9 = Movie(
            id = 1,
            title = "Movie 1",
            releaseDate = "2021-06-15T00:00:00.000Z"
        )
        assertEquals("2021", movie9.getReleaseYear())
    }

    @Test
    fun `getDirectorsString should return the right string`() {
        val movie = Movie(
            id = 1,
            title = "Movie 1",
            credits = Credits(
                crew = listOf(
                    CrewMember(id = 1, name = "Director 1", department = "Directing"),
                )
            )
        )
        assertEquals("Director 1", movie.getDirectorsString("and"))

        val movie2 = Movie(
            id = 1,
            title = "Movie 1",
            credits = Credits(
                crew = listOf(
                    CrewMember(id = 1, name = "Director 1", department = "Directing"),
                    CrewMember(id = 1, name = "Director 2", department = "Directing"),
                )
            )
        )
        assertEquals("Director 1 and Director 2", movie2.getDirectorsString("and"))

        val movie3 = Movie(
            id = 1,
            title = "Movie 1",
            credits = Credits(
                crew = listOf(
                    CrewMember(id = 1, name = "Director 1", department = "Directing"),
                    CrewMember(id = 1, name = "Director 2", department = "Directing"),
                    CrewMember(id = 1, name = "Director 3", department = "Directing"),
                )
            )
        )
        assertEquals("Director 1, Director 2 and Director 3", movie3.getDirectorsString("and"))

        val movie4 = Movie(
            id = 1,
            title = "Movie 1",
            credits = Credits(
                crew = listOf(
                    CrewMember(id = 1, name = "Director 1", department = "Directing"),
                    CrewMember(id = 1, name = "Director 2", department = "Directing"),
                    CrewMember(id = 1, name = "Director 3", department = "Directing"),
                    CrewMember(id = 1, name = "Director 4", department = "Directing"),
                )
            )
        )
        assertEquals(
            "Director 1, Director 2, Director 3 and Director 4",
            movie4.getDirectorsString("and")
        )

        val noDirectorsMovie = Movie(
            id = 1,
            title = "Movie 1",
            credits = Credits(
                crew = emptyList()
            )
        )
        assertEquals("", noDirectorsMovie.getDirectorsString("and"))
    }
}