package dk.soerensen.garbagev1.domain

import kotlinx.coroutines.flow.Flow

interface ItemRepository {

    fun getItems(): Flow<List<GarbageItem>>

    suspend fun remove(item: GarbageItem)

    suspend fun add(item: GarbageItem)

    suspend fun findBin(name: String): String?

    fun getItem(id: String): Flow<GarbageItem?>

    suspend fun updateItem(item: GarbageItem)
}