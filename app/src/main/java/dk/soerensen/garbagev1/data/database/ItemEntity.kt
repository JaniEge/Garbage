package dk.soerensen.garbagev1.data.database

data class ItemEntity(
    val id: String = "",
    val title: String = "",    // Ændret fra name -> title
    val binId: String = "",    // Ændret fra bin -> binId
    val description: String = "",
    val imageUri: String = ""
)