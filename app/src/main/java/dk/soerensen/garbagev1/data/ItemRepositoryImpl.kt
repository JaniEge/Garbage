package dk.soerensen.garbagev1.data

import dk.soerensen.garbagev1.data.database.ItemDao
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.ItemRepository
import dk.soerensen.garbagev1.domain.toDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import dk.soerensen.garbagev1.data.database.toGarbageItemDto

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao
) : ItemRepository {

    override fun getItems(): Flow<List<GarbageItem>> {
        return itemDao.getItems().map { entityList ->
            entityList.map { it.toGarbageItemDto().toItem() }
        }
    }

    override suspend fun remove(item: GarbageItem) {
        itemDao.delete(id = item.id)
    }

    override suspend fun add(item: GarbageItem) {
        val formattedItem = item.copy(name = item.name.toTitleCase(), bin = item.bin.toTitleCase())
        itemDao.insert(item = formattedItem.toDto().toEntity())
    }

    override suspend fun findBin(name: String): String? {
        val q = name.trim()
        if (q.isBlank()) return null
        return itemDao.findByName(q)?.bin
    }

    override fun getItem(id: String): Flow<GarbageItem?> {
        return itemDao.getItem(id = id).map { it?.toGarbageItemDto()?.toItem() }
    }

    override suspend fun updateItem(item: GarbageItem) {
        val formattedItem = item.copy(name = item.name.toTitleCase(), bin = item.bin.toTitleCase())
        itemDao.update(item = formattedItem.toDto().toEntity())
    }

    private fun String.toTitleCase(): String {
        return this.trim().split(" ").joinToString(separator = " ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}