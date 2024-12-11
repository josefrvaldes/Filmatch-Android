package es.josevaldes.local.dao

import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase
import es.josevaldes.local.entities.VisitedFiltersEntity
import es.josevaldes.local.entities.VisitedMovieEntity

@Database(
    entities = [VisitedFiltersEntity::class, VisitedMovieEntity::class],
    version = 1,
    exportSchema = false
)
@Dao
abstract class LocalDatabase : RoomDatabase() {
    abstract fun visitedFiltersDao(): VisitedFiltersDao
    abstract fun visitedMoviesDao(): VisitedMoviesDao
}