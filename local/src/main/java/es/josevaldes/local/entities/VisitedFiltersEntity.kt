package es.josevaldes.local.entities

import androidx.room.Entity


@Entity(tableName = "visited_filters", primaryKeys = ["id"])
data class VisitedFiltersEntity(
    val filtersHash: String,
    val maxPage: Int
)

