package es.josevaldes.local.converters

import androidx.room.TypeConverter
import es.josevaldes.local.entities.MediaEntityType

class Converters {
    @TypeConverter
    fun fromGenreIds(value: List<Int>): String = value.joinToString(",")

    @TypeConverter
    fun toGenreIds(value: String): List<Int> =
        if (value.isEmpty()) emptyList() else value.split(",").map { it.toInt() }

    @TypeConverter
    fun fromMediaType(value: MediaEntityType): String = value.name

    @TypeConverter
    fun toMediaType(value: String): MediaEntityType = MediaEntityType.valueOf(value)
}