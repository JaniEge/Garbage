package dk.soerensen.garbagev1.domain

import dk.soerensen.garbagev1.data.GarbageItemDto

data class GarbageItem(
    val name: String,
    val bin: String
)

fun GarbageItem.toDto(): GarbageItemDto =
    GarbageItemDto(
        name = this.name,
        bin = this.bin
    )
