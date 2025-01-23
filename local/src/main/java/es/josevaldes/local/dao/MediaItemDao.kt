package es.josevaldes.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.josevaldes.local.entities.MediaEntityType
import es.josevaldes.local.entities.MediaItemEntity

@Dao
interface DiscoverItemDao {
    @Query("SELECT * FROM discover_items WHERE type = :type")
    fun getItemsByType(type: MediaEntityType): List<MediaItemEntity>

    @Query("SELECT * FROM discover_items WHERE id = :id AND type = :type")
    fun getItemById(id: Int, type: MediaEntityType): MediaItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<MediaItemEntity>)
}