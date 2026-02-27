package dk.soerensen.garbagev1.domain

import kotlinx.coroutines.flow.Flow
interface BinRepository {
    fun getBins(): List<Bin>
    fun getBin(id: String): Bin?
}