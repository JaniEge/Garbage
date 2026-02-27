package dk.soerensen.garbagev1.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ItemRepository {


    val items: StateFlow<List<GarbageItem>>


    fun getItems(): Flow<List<GarbageItem>> = items


    fun remove(item: GarbageItem): Int


    fun add(index: Int, item: GarbageItem)


    fun findBin(name: String): String?


    fun getItem(id: String): Flow<GarbageItem?>


    suspend fun updateItem(item: GarbageItem)
}