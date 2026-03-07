package dk.soerensen.garbagev1.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bins")
data class BinEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String
)