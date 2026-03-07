package dk.soerensen.garbagev1.domain

import kotlinx.coroutines.flow.Flow

interface BinRepository {
    fun getBins(): Flow<List<Bin>>
    fun getBin(id: String): Flow<Bin?>
}