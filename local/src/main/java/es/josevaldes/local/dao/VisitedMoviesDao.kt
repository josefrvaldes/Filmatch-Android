package es.josevaldes.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import es.josevaldes.local.entities.VisitedFiltersEntity
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

    @Query("SELECT EXISTS(SELECT * FROM visited_movies WHERE id = :movieId)")
    suspend fun isMovieVisited(movieId: String): Boolean

    @Query("DELETE FROM visited_movies WHERE id = :movieId")
    suspend fun deleteVisitedMovie(movieId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitedFilters(visitedFilters: VisitedFiltersEntity)

    @Query("UPDATE visited_filters SET maxPage = :maxPage WHERE filtersHash = :filtersHash")
    suspend fun updateMaxPage(filtersHash: String, maxPage: Int)

    @Query("SELECT maxPage FROM visited_filters WHERE filtersHash = :filtersHash")
    suspend fun getMaxPage(filtersHash: String): Int?

    @Query("DELETE FROM visited_filters WHERE filtersHash = :filtersHash")
    suspend fun deleteVisitedFilters(filtersHash: String)
}