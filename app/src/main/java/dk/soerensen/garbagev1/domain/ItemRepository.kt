package dk.soerensen.garbagev1.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ItemRepository {

    // Beholdes: din app bruger StateFlow direkte flere steder (fx undo/remove).
    val items: StateFlow<List<GarbageItem>>

    // NY (inspiration fra Shopping): let at observe som Flow samme mønster som getShoppingList()
    fun getItems(): Flow<List<GarbageItem>> = items

    // Beholdes: undo workflow (returnerer index)
    fun remove(item: GarbageItem): Int

    // Beholdes: undo workflow (indsæt igen på index)
    fun add(index: Int, item: GarbageItem)

    // Beholdes: bin lookup feature
    fun findBin(name: String): String?

    // Krævet af opgaven: opslag via id
    fun getItem(id: String): Flow<GarbageItem?>

    // Krævet af opgaven: update via id
    suspend fun updateItem(item: GarbageItem)
}