package dk.soerensen.garbagev1.data.database

import com.google.firebase.firestore.PropertyName

data class BinEntity(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var lastPickupTime: Long = 0L,
    val count: Int = 0,
    @get:PropertyName("title_da") @set:PropertyName("title_da")
    var titleDa: String = "",
    @get:PropertyName("title_en") @set:PropertyName("title_en")
    var titleEn: String = "",
    @get:PropertyName("description_da") @set:PropertyName("description_da")
    var descriptionDa: String = "",
    @get:PropertyName("description_en") @set:PropertyName("description_en")
    var descriptionEn: String = ""
) {
    // En tom konstruktør er nødvendig for at Firebase kan indlæse data
    constructor() : this("", "", "", "", 0L, 0, "", "", "", "")
}