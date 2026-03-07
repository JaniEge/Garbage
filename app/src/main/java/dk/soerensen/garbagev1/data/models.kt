package dk.soerensen.garbagev1.data

import dk.soerensen.garbagev1.data.database.ItemEntity
import dk.soerensen.garbagev1.domain.GarbageItem
import java.util.UUID

data class GarbageItemDto(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bin: String,
    val description: String = ""
)

fun GarbageItemDto.toItem(): GarbageItem =
    GarbageItem(
        id = id,
        name = name,
        bin = bin,
        description = description
    )

fun GarbageItemDto.toEntity(): ItemEntity =
    ItemEntity(
        id = id,
        name = name,
        bin = bin,
        description = description
    )