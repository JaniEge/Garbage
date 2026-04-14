package dk.soerensen.garbagev1.data.database

data class BinEntity(
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