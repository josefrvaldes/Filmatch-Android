package es.josevaldes.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import es.josevaldes.local.entities.VisitedMovieEntity

@Dao
interface VisitedMoviesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitedMovie(visitedMovie: VisitedMovieEntity)

    @Transaction
    suspend fun insertVisitedMovies(visitedMovies: List<VisitedMovieEntity>) {
        visitedMovies.forEach { insertVisitedMovie(it) }
    }

    @Transaction
    @Query("SELECT * FROM visited_movies")
    suspend fun getVisitedMovies(): List<VisitedMovieEntity>
}