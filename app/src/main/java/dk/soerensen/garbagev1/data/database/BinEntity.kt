package dk.soerensen.garbagev1.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bins")
data class BinEntity(
    @PrimaryKey
    var id: String = "",         // Skift fra 'val' til 'var' for Firebase
    var title: String = "",
    var description: String = "",
    var imageUrl: String = "",   // 👈 Skal matche 'imageUrl' i Firebase 1:1
    var lastPickupTime: Long = 0L,
    val count: Int = 0
) {
    // En tom konstruktør er nødvendig for at Firebase kan indlæse data
    constructor() : this("", "", "", "", 0L)
}