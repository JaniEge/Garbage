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
    val lastPickupTime: Long = 0L // 👈 TILFØJ DENNE LINJE
)
data class RecyclingStation(

val id: String = "",
val name: String = "",
val category: String = "",
val address: String = "",
val status: String = "",
val bins: List<String> = emptyList(),
val latitude: Double = 0.0,
val longitude: Double = 0.0
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