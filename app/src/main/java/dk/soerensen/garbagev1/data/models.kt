package dk.soerensen.garbagev1.data

import dk.soerensen.garbagev1.domain.GarbageItem

data class GarbageItemDto(
    val name: String,
    val bin: String
)

fun GarbageItemDto.toItem(): GarbageItem =
    GarbageItem(
        name = this.name,
        bin = this.bin
    )