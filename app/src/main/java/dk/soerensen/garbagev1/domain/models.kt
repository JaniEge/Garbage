package dk.soerensen.garbagev1.domain

import dk.soerensen.garbagev1.data.GarbageItemDto
import java.util.UUID

data class GarbageItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bin: String
)

data class Bin(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String
)

fun GarbageItem.toDto(): GarbageItemDto =
    GarbageItemDto(
        id = id,
        name = name,
        bin = bin
    )

fun GarbageItem.fullDescription(): String =
    "${name.trim()} → ${bin.trim()}"

