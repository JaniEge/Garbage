package dk.soerensen.garbagev1.data

import com.google.firebase.firestore.FirebaseFirestore
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.ItemRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ItemRepository {

    private val itemsCollection = firestore.collection("items")

    override fun getItems(): Flow<List<GarbageItem>> = callbackFlow {
        val subscription = itemsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(GarbageItem::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(items).isSuccess
        }

        awaitClose { subscription.remove() }
    }

    override fun getItem(id: String): Flow<GarbageItem?> = callbackFlow {
        val subscription = itemsCollection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val item = if (snapshot != null && snapshot.exists()) {
                    snapshot.toObject(GarbageItem::class.java)?.copy(id = snapshot.id)
                } else {
                    null
                }

                trySend(item).isSuccess
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun add(item: GarbageItem) {
        val formattedItem = item.copy(
            name = item.name.toTitleCase()
        )
        itemsCollection.document(formattedItem.id).set(formattedItem).await()
    }

    override suspend fun updateItem(item: GarbageItem) {
        val formattedItem = item.copy(
            name = item.name.toTitleCase()
        )
        itemsCollection.document(formattedItem.id).set(formattedItem).await()
    }

    override suspend fun remove(item: GarbageItem) {
        itemsCollection.document(item.id).delete().await()
    }

    override suspend fun findBin(name: String): String? {
        val formattedName = name.trim().toTitleCase()

        val querySnapshot = itemsCollection
            .whereEqualTo("name", formattedName)
            .get()
            .await()

        return querySnapshot.documents.firstOrNull()?.getString("bin")
    }

    private fun String.toTitleCase(): String {
        return trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { it.uppercase() }
            }
    }
}