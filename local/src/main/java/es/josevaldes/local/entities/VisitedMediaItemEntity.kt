package es.josevaldes.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

@Entity(
    tableName = "visited_medias",
    primaryKeys = ["mediaId", "type"],
    foreignKeys = [
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = ["id", "type"],
            childColumns = ["mediaId", "type"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mediaId", "type"]),
        Index(value = ["type"])
    ]
)
data class VisitedMediaItemEntity(
    val mediaId: Int,
    val type: MediaEntityType,
    val interestStatus: InterestStatus
)


data class VisitedMediaWithItem(
    @Embedded val visitedMedia: VisitedMediaItemEntity,
    @Relation(
        parentColumn = "mediaId",
        entityColumn = "id",
        entity = MediaItemEntity::class,
        associateBy = Junction(
            value = VisitedMediaItemEntity::class,
            parentColumn = "type",
            entityColumn = "type"
        )
    )
    val mediaItem: MediaItemEntity
)