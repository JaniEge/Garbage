package dk.soerensen.garbagev1.data

import dk.soerensen.garbagev1.data.database.ItemEntity
import dk.soerensen.garbagev1.domain.GarbageItem
import kotlinx.serialization.Serializable
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

@Serializable
data class RecyclingFeature(
    val id: String? = null,
    val navn: String? = null,
    val kategori: String? = null,
    val adresse: String? = null,
    val status: String? = null,
    val fraktioner: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null
)