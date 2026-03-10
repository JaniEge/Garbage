package dk.soerensen.garbagev1.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dk.soerensen.garbagev1.data.GarbageItemDto

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val bin: String,
    val description: String,
)

fun ItemEntity.toGarbageItemDto(): GarbageItemDto =
    GarbageItemDto(id = id, name = name, bin = bin, description = description)