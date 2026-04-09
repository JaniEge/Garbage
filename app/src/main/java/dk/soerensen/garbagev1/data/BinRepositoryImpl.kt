package dk.soerensen.garbagev1.data

import com.google.firebase.firestore.FirebaseFirestore
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
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val bins = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Bin::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(bins).isSuccess
            }

        awaitClose { subscription.remove() }
    }

    override fun getBin(id: String): Flow<Bin?> = callbackFlow {
        val subscription = binsCollection
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val bin = if (snapshot != null && snapshot.exists()) {
                    snapshot.toObject(Bin::class.java)?.copy(id = snapshot.id)
                } else {
                    null
                }

                trySend(bin).isSuccess
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun updateBin(bin: Bin) {
        binsCollection.document(bin.id).set(bin).await()
    }
}