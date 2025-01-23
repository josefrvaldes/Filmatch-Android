package es.josevaldes.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import es.josevaldes.local.entities.InterestStatus
import es.josevaldes.local.entities.MediaEntityType
import es.josevaldes.local.entities.MediaItemEntity
import es.josevaldes.local.entities.VisitedFiltersEntity
import es.josevaldes.local.entities.VisitedMediaItemEntity
import es.josevaldes.local.entities.VisitedMediaWithItem

@Dao
interface VisitedMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: MediaItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitedMedia(visitedMedia: VisitedMediaItemEntity)

    @Transaction
    suspend fun insertVisitedMediaWithItem(
        visitedMedia: VisitedMediaWithItem
    ) {
        insertMediaItem(visitedMedia.mediaItem)
        insertVisitedMedia(visitedMedia.visitedMedia)
    }

    @Transaction
    @Query("SELECT * FROM visited_medias WHERE interestStatus = :status")
    suspend fun getVisitedMediaByStatus(status: InterestStatus): List<VisitedMediaWithItem>

    @Transaction
    @Query("SELECT * FROM visited_medias WHERE type = :type")
    suspend fun getVisitedMediaByType(type: MediaEntityType): List<VisitedMediaWithItem>

    @Delete
    suspend fun deleteVisitedMedia(visitedMedia: VisitedMediaItemEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM visited_medias WHERE mediaId = :mediaId AND type = :type)")
    suspend fun isVisitedMedia(mediaId: Int, type: MediaEntityType): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitedFilters(visitedFilters: VisitedFiltersEntity)

    @Query("UPDATE visited_filters SET maxPage = :maxPage WHERE filtersHash = :filtersHash")
    suspend fun updateMaxPage(filtersHash: String, maxPage: Int)

    @Query("SELECT maxPage FROM visited_filters WHERE filtersHash = :filtersHash")
    suspend fun getMaxPage(filtersHash: String): Int?

    @Query("DELETE FROM visited_filters WHERE filtersHash = :filtersHash")
    suspend fun deleteVisitedFilters(filtersHash: String)
}