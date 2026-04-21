package dk.soerensen.garbagev1.data

import com.google.firebase.firestore.FirebaseFirestore
import dk.soerensen.garbagev1.data.database.BinEntity
import dk.soerensen.garbagev1.domain.Bin
import dk.soerensen.garbagev1.domain.BinRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BinRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BinRepository {

    private val binsCollection = firestore.collection("bins")

    private fun useDanish(): Boolean = Locale.getDefault().language == "da"

    override fun getBins(): Flow<List<Bin>> = callbackFlow {
        val subscription = binsCollection
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val bins = snapshot.documents.mapNotNull { doc ->
                        val entity = doc.toObject(BinEntity::class.java)
                        entity?.copy(id = doc.id)?.toBin()
                    }.sortedBy { it.title }
                    trySend(bins)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getBin(id: String): Flow<Bin?> = callbackFlow {
        val subscription = binsCollection.document(id).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val entity = snapshot.toObject(BinEntity::class.java)
                val bin = entity?.copy(id = snapshot.id)?.toBin()
                trySend(bin)
            } else {
                trySend(null)
            }
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateBin(bin: Bin) {
        binsCollection.document(bin.id).update(
            mapOf(
                "lastPickupTime" to bin.lastPickupTime,
                "count" to bin.count
            )
        ).await()
    }

    // --- Hjælpefunktioner til konvertering ---

    private fun resolveField(primary: String, legacy: String, fallback: String): String =
        primary.ifBlank { legacy.ifBlank { fallback } }

    private fun BinEntity.toBin(): Bin {
        val danish = useDanish()
        val resolvedTitle = if (danish) {
            resolveField(titleDa, title, titleEn)
        } else {
            resolveField(titleEn, title, titleDa)
        }
        val resolvedDescription = if (danish) {
            resolveField(descriptionDa, description, descriptionEn)
        } else {
            resolveField(descriptionEn, description, descriptionDa)
        }
        return Bin(
            id = id,
            title = resolvedTitle,
            description = resolvedDescription,
            imageUrl = imageUrl,
            lastPickupTime = lastPickupTime,
            count = count
        )
    }
}