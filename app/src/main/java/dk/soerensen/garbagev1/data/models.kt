package dk.soerensen.garbagev1.data

import dk.soerensen.garbagev1.domain.GarbageItem
import kotlinx.serialization.SerialName
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


@Serializable
data class RecyclingStationsResponse(
    val type: String,
    val features: List<RecyclingFeature> = emptyList()
)

@Serializable
data class RecyclingFeature(
    val type: String,
    val id: String,
    val geometry: RecyclingGeometry,
    @SerialName("geometry_name")
    val geometryName: String? = null,
    val properties: RecyclingProperties
)

@Serializable
data class RecyclingGeometry(
    val type: String,
    val coordinates: List<List<Double>> = emptyList()
)

@Serializable
data class RecyclingProperties(
    val id: Int,
    val navn: String? = null,
    val kategori: String? = null,
    val adresse: String? = null,
    val status: String? = null,
    val anvendelse: String? = null,
    @SerialName("bytte_dele_zone")
    val bytteDeleZone: String? = null,
    val lukket: String? = null,
    @SerialName("aabningstider_1")
    val aabningstider1: String? = null,
    @SerialName("aabningstider_2")
    val aabningstider2: String? = null,
    @SerialName("aabningstider_3")
    val aabningstider3: String? = null,
    val fraktioner: String? = null,
    val link: String? = null,
    @SerialName("reg_dato")
    val regDato: String? = null,
    @SerialName("rettet_dato")
    val rettetDato: String? = null
)