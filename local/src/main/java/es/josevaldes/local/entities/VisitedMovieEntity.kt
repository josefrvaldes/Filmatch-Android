package es.josevaldes.local.entities

import androidx.room.Entity

@Entity(tableName = "visited_movies", primaryKeys = ["id"])
data class VisitedMovieEntity(
    val id: String,
)
