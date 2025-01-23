package es.josevaldes.local.dao

import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase
import es.josevaldes.local.entities.MediaItemEntity
import es.josevaldes.local.entities.VisitedFiltersEntity
import es.josevaldes.local.entities.VisitedMediaItemEntity

@Database(
    entities = [VisitedFiltersEntity::class, VisitedMediaItemEntity::class, MediaItemEntity::class],
    version = 1,
    exportSchema = false
)
@Dao
abstract class LocalDatabase : RoomDatabase() {
    abstract fun visitedMediaDao(): VisitedMediaDao
}