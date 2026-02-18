package dk.soerensen.garbagev1.domain

import kotlinx.coroutines.flow.StateFlow

interface ItemRepository {
    val items: StateFlow<List<GarbageItem>>
    fun remove(item: GarbageItem): Int
    fun add(index: Int, item: GarbageItem)
    fun findBin(name: String): String?
}