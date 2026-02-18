package dk.soerensen.garbagev1.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.soerensen.garbagev1.R
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ItemRepository  {

    private val _items = MutableStateFlow(loadFromRaw())
    override val items: StateFlow<List<GarbageItem>> = _items.asStateFlow()

    private fun loadFromRaw(): List<GarbageItem> {
        val list = context.resources
            .openRawResource(R.raw.garbage)
            .bufferedReader()
            .useLines { lines ->
                lines.mapNotNull { line ->
                    val parts = line.split(",")
                    if (parts.size >= 2) {
                        GarbageItem(
                            name = parts[0].trim(),
                            bin = parts[1].trim()
                        )
                    } else null
                }.toList()
            }


        return list.sortedBy { it.name.lowercase() }
    }

    override fun remove(item: GarbageItem): Int {
        val current = _items.value.toMutableList()
        val index = current.indexOf(item)
        if (index >= 0) {
            current.removeAt(index)
            _items.value = current
        }
        return index
    }

    override fun add(index: Int, item: GarbageItem) {
        val current = _items.value.toMutableList()
        val safeIndex = index.coerceIn(0, current.size)
        current.add(safeIndex, item)
        _items.value = current
    }

    override fun findBin(name: String): String? {
        val q = name.trim()
        return _items.value
            .firstOrNull { it.name.equals(q, ignoreCase = true) }
            ?.bin
    }
}

