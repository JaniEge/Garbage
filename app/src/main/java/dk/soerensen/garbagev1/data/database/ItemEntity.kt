package dk.soerensen.garbagev1.data.database

import com.google.firebase.firestore.PropertyName

data class ItemEntity(
    val id: String = "",
    val title: String = "",
    val binId: String = "",
    val description: String = "",
    val imageUri: String = "",
    @get:PropertyName("title_en") @set:PropertyName("title_en")
    var titleEn: String = "",
    @get:PropertyName("description_en") @set:PropertyName("description_en")
    var descriptionEn: String = ""
) {
    constructor() : this("", "", "", "", "", "", "")
}