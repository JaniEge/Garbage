package dk.soerensen.garbagev1.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.soerensen.garbagev1.R
import dk.soerensen.garbagev1.data.database.ItemDao
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ItemRepository {

    private val _items = MutableStateFlow(loadFromRaw())
    override val items: StateFlow<List<GarbageItem>> = _items.asStateFlow()

    private fun loadFromRaw(): List<GarbageItem> {
        val list = context.resources
            .openRawResource(R.raw.garbage)
            .bufferedReader()
            .useLines { lines ->
                lines.mapNotNull { line ->
                    // Tillad lidt robusthed: tomme linjer / whitespace
                    val trimmed = line.trim()
                    if (trimmed.isBlank()) return@mapNotNull null

                    // CSV (forventer "name,bin")
                    val parts = trimmed.split(",")
                    if (parts.size < 2) return@mapNotNull null

                    val name = parts[0].trim()
                    val bin = parts[1].trim()

                    if (name.isBlank() || bin.isBlank()) return@mapNotNull null

                    GarbageItem(
                        id = UUID.randomUUID().toString(),
                        name = name.toTitleCase(),
                        bin = bin
                    )
                }.toList()
            }

        // Startliste sorteret alfabetisk
        return list.sortedBy { it.name.lowercase() }
    }

    override fun remove(item: GarbageItem): Int {
        val current = _items.value
        val index = current.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            _items.update { list -> list.filterNot { it.id == item.id } }
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
        if (q.isBlank()) return null

        return _items.value
            .firstOrNull { it.name.equals(q, ignoreCase = true) }
            ?.bin
    }

    override fun getItem(id: String): Flow<GarbageItem?> {
        val q = id.trim()
        return items.map { list ->
            if (q.isBlank()) null
            else list.firstOrNull { it.id == q }
                ?: list.firstOrNull { it.name.equals(q, ignoreCase = true) } // fallback to name
        }
    }

    override suspend fun updateItem(item: GarbageItem) {
        _items.update { current ->
            val index = current.indexOfFirst { it.id == item.id }
            if (index < 0) {
                current
            } else {
                current.mapIndexed { i, existing ->
                    if (i == index) {
                        item.copy(name = item.name.toTitleCase())
                    } else existing
                }
            }
        }
    }

    private fun String.toTitleCase(): String {
        return trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.lowercase()
                    .replaceFirstChar { ch ->
                        if (ch.isLowerCase()) ch.titlecase() else ch.toString()
                    }
            }
    }
}