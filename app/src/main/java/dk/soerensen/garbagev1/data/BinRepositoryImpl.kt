package dk.soerensen.garbagev1.data

import com.google.firebase.firestore.FirebaseFirestore
import dk.soerensen.garbagev1.data.database.BinEntity
import dk.soerensen.garbagev1.domain.Bin
import dk.soerensen.garbagev1.domain.BinRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BinRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BinRepository {

    private val binsCollection = firestore.collection("bins")

    override fun getBins(): Flow<List<Bin>> = callbackFlow {
        val subscription = binsCollection
            .orderBy("title")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val bins = snapshot.documents.mapNotNull { doc ->
                        // Ved at bruge doc.id sikrer vi, at ID'et fra Firebase (bio, glass, etc.)
                        // bliver brugt som nøgle til dine billeder!
                        val entity = doc.toObject(BinEntity::class.java)
                        entity?.copy(id = doc.id)?.toBin()
                    }
                    trySend(bins)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getBin(id: String): Flow<Bin?> = callbackFlow {
        val subscription = binsCollection.document(id).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                // Her skal vi også huske at snuppe id'et fra dokumentet
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
        // Her gemmer vi hele objektet (inklusiv den nye count) tilbage til Firebase
        binsCollection.document(bin.id).set(bin.toEntity()).await()
    }

    // --- Hjælpefunktioner til konvertering ---
    // Her skal 'count' tilføjes i begge retninger!

    private fun BinEntity.toBin() = Bin(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        lastPickupTime = lastPickupTime,
        count = count // 👈 TILFØJ DENNE
    )

    private fun Bin.toEntity() = BinEntity(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        lastPickupTime = lastPickupTime,
        count = count // 👈 TILFØJ DENNE
    )
}