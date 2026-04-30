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

    private fun isDanish(): Boolean = Locale.getDefault().language == "da"

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

        val snapshot = itemsCollection.get().await()
        return snapshot.toObjects(ItemEntity::class.java).firstOrNull { entity ->
            val compareTitle = if (isDanish()) {
                entity.title
            } else {
                entity.titleEn.ifBlank { entity.title }
            }
            compareTitle.trim().lowercase() == q
        }?.binId
    }

    // --- Hjælpefunktioner til konvertering ---

    private fun getLocalizedText(da: String, en: String): String {
        return if (isDanish()) da.ifBlank { en } else en.ifBlank { da }
    }

    private fun ItemEntity.toItem() = GarbageItem(
        id = id,
        name = getLocalizedText(title, titleEn),
        bin = binId,
        description = getLocalizedText(description, descriptionEn),
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