package dk.soerensen.garbagev1.data

import com.google.firebase.firestore.FirebaseFirestore
import dk.soerensen.garbagev1.data.database.ItemEntity
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.ItemRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore // ✅ Skiftet fra ItemDao
) : ItemRepository {

    private val itemsCollection = firestore.collection("items")

    override fun getItems(): Flow<List<GarbageItem>> = callbackFlow {
        val subscription = itemsCollection.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val items = snapshot.toObjects(ItemEntity::class.java).map { it.toItem() }
                trySend(items)
            }
        }
        awaitClose { subscription.remove() }
    }

    override fun getItem(id: String): Flow<GarbageItem?> = callbackFlow {
        val subscription = itemsCollection.document(id).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val item = snapshot.toObject(ItemEntity::class.java)?.toItem()
                trySend(item)
            } else {
                trySend(null)
            }
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun add(item: GarbageItem) {
        val formattedItem = item.copy(name = item.name.toTitleCase(), bin = item.bin.toTitleCase())
        // Vi bruger dokument-ID som item.id
        itemsCollection.document(formattedItem.id).set(formattedItem.toEntity()).await()
    }

    override suspend fun updateItem(item: GarbageItem) {
        val formattedItem = item.copy(name = item.name.toTitleCase(), bin = item.bin.toTitleCase())
        itemsCollection.document(formattedItem.id).set(formattedItem.toEntity()).await()
    }

    override suspend fun remove(item: GarbageItem) {
        itemsCollection.document(item.id).delete().await()
    }

    override suspend fun findBin(name: String): String? {
        val q = name.trim().lowercase()
        if (q.isBlank()) return null

        val isDanish = Locale.getDefault().language == "da"

        // Preferred: query by normalized titleKey_da / titleKey_en (case-insensitive)
        val titleKeyField = if (isDanish) "titleKey_da" else "titleKey_en"
        val preferredQuery = itemsCollection.whereEqualTo(titleKeyField, q).get().await()
        val preferredBinId = preferredQuery.documents.firstOrNull()?.getString("binId")
        if (preferredBinId != null) return preferredBinId

        // Fallback: query by title / title_en with TitleCase
        val titleField = if (isDanish) "title" else "title_en"
        val fallbackQuery = itemsCollection.whereEqualTo(titleField, q.toTitleCase()).get().await()
        return fallbackQuery.documents.firstOrNull()?.getString("binId")
    }

    // --- Hjælpefunktioner til konvertering ---

    private fun ItemEntity.toItem() = GarbageItem(
        id = id,
        name = title,
        bin = binId,
        description = description,
        imageUri = imageUri
    )

    private fun GarbageItem.toEntity() = ItemEntity(
        id = id,
        title = name,
        binId = bin,
        description = description,
        imageUri = imageUri
    )

    private fun String.toTitleCase(): String {
        return this.trim().split(" ").joinToString(separator = " ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}