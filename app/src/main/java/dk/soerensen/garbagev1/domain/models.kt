package dk.soerensen.garbagev1.domain

import dk.soerensen.garbagev1.data.GarbageItemDto
import java.util.UUID

data class GarbageItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bin: String,
    val description: String = "",
    // TODO: Add deadline to item
)

data class Bin(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String
)
enum class Theme {
    SYSTEM,
    LIGHT,
    DARK
}

fun GarbageItem.toDto(): GarbageItemDto = GarbageItemDto(id = this.id, name = this.name, bin = this.bin, description = this.description)
//fun Shop.toDto(): ShopDto = ShopDto(name = this.name, imageUrl = this.imageUrl, brandColor = this.brandColor.value.toString())

fun GarbageItem.fullDescription(): String =
    "${name.trim()} → ${bin.trim()}"

