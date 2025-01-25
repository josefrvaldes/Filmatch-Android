package es.josevaldes.local.converters

import androidx.room.TypeConverter
import es.josevaldes.local.entities.InterestStatus
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

    @TypeConverter
    fun fromInterestStatus(value: InterestStatus): Int {
        return when (value) {
            InterestStatus.INTERESTED -> 0
            InterestStatus.SUPER_INTERESTED -> 1
            InterestStatus.NOT_INTERESTED -> 2
            InterestStatus.WATCHED -> 3
            InterestStatus.NONE -> -1
        }
    }

    @TypeConverter
    fun toInterestStatus(value: Int): InterestStatus {
        return when (value) {
            0 -> InterestStatus.INTERESTED
            1 -> InterestStatus.SUPER_INTERESTED
            2 -> InterestStatus.NOT_INTERESTED
            3 -> InterestStatus.WATCHED
            else -> InterestStatus.NONE
        }
    }
}