package dk.soerensen.garbagev1.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String = "",
    val title: String = "",    // Ændret fra name -> title
    val binId: String = "",    // Ændret fra bin -> binId
    val description: String = ""
)