package dk.soerensen.garbagev1.domain

import dk.soerensen.garbagev1.data.GarbageItemDto
import java.util.UUID

data class GarbageItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bin: String,
    val description: String = "",
)

data class Bin(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val lastPickupTime: Long = 0L,
    val count: Int = 0 // 👈 Tilføj denne linje (fjern teksten her når du har skrevet det)
)
data class RecyclingStation(
    val id: String,
    val name: String,
    val category: String,
    val address: String,
    val status: String,
    val bins: List<String>,
    val latitude: Double,
    val longitude: Double,
)

enum class Theme {
    SYSTEM,
    LIGHT,
    DARK
}

fun GarbageItem.toDto(): GarbageItemDto =
    GarbageItemDto(
        id = this.id,
        name = this.name,
        bin = this.bin,
        description = this.description
    )

fun GarbageItem.fullDescription(): String =
    "${name.trim()} → ${bin.trim()}"