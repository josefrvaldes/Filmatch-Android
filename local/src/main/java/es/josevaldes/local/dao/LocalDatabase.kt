package es.josevaldes.local.dao

import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.josevaldes.local.converters.Converters
import es.josevaldes.local.entities.MediaItemEntity
import es.josevaldes.local.entities.VisitedFiltersEntity
import es.josevaldes.local.entities.VisitedMediaItemEntity

@Database(
    entities = [VisitedFiltersEntity::class, VisitedMediaItemEntity::class, MediaItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
@Dao
abstract class LocalDatabase : RoomDatabase() {
    abstract fun visitedMediaDao(): VisitedMediaDao
}