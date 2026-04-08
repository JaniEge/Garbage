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
    private val firestore: FirebaseFirestore // ✅ Skiftet fra BinDao
) : BinRepository {

    private val binsCollection = firestore.collection("bins")

    override fun getBins(): Flow<List<Bin>> = callbackFlow {
        // Vi sorterer efter titel, så de altid står pænt (f.eks. alfabetisk)
        val subscription = binsCollection
            .orderBy("title")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val bins = snapshot.toObjects(BinEntity::class.java).map { it.toBin() }
                    trySend(bins)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getBin(id: String): Flow<Bin?> = callbackFlow {
        val subscription = binsCollection.document(id).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val bin = snapshot.toObject(BinEntity::class.java)?.toBin()
                trySend(bin)
            } else {
                trySend(null)
            }
        }
        awaitClose { subscription.remove() }
    }

    // Tilføj denne hvis dit interface kræver det (V3.2 krav om opdatering af tid)
    override suspend fun updateBin(bin: Bin) {
        binsCollection.document(bin.id).set(bin.toEntity()).await()
    }

    // --- Hjælpefunktioner til konvertering ---

    private fun BinEntity.toBin() = Bin(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        lastPickupTime = lastPickupTime // ✅ Sørg for at din Domain Bin model har dette felt
    )

    private fun Bin.toEntity() = BinEntity(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        lastPickupTime = lastPickupTime
    )
}