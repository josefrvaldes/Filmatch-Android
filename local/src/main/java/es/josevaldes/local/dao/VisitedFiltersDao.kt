package es.josevaldes.local.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.josevaldes.local.entities.VisitedFiltersEntity

interface VisitedFiltersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitedFilters(visitedFilters: VisitedFiltersEntity)

    @Query("UPDATE visited_filters SET maxPage = :maxPage WHERE filtersHash = :filtersHash")
    suspend fun updateMaxPage(filtersHash: String, maxPage: Int)

    @Query("SELECT maxPage FROM visited_filters WHERE filtersHash = :filtersHash")
    suspend fun getMaxPage(filtersHash: String): Int?
}