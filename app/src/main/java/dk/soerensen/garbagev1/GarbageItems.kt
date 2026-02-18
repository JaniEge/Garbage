package dk.soerensen.garbagev1

import android.content.Context
import androidx.compose.runtime.mutableStateListOf

data class GarbageItem(
        val name: String,
        val bin: String
    )

    object GarbageItems {

        private val garbageList = mutableStateListOf<GarbageItem>()

        fun loadFromRaw(context: Context) {
            garbageList.clear()

            val inputStream = context.resources.openRawResource(R.raw.garbage)
            inputStream.bufferedReader().useLines { lines ->
                lines
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .forEach { line ->
                        val parts = line.split(",")
                        if (parts.size >= 2) {
                            val name = parts[0].trim()
                            val bin = parts[1].trim()
                            garbageList.add(GarbageItem(name, bin))
                        }
                    }
            }
            garbageList.sortBy { it.name.lowercase() }
        }

        fun findBin(garbageName: String): String? {
            return garbageList
                .find { it.name.equals(garbageName.trim(), ignoreCase = true) }
                ?.bin
        }

        fun asTextLines(): List<String> {
            return garbageList.map {
                "${it.name} should be placed in: ${it.bin}"
            }
        }

        fun delete(item: GarbageItem): Int {
            val index = garbageList.indexOf(item)
            if (index >= 0) {
                garbageList.removeAt(index)
            }
            return index
        }

        fun insertAt(index: Int, item: GarbageItem) {
            val safeIndex = index.coerceIn(0, garbageList.size)
            garbageList.add(safeIndex, item)
        }
        fun getAll(): List<GarbageItem> = garbageList
    }
