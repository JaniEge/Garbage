package dk.soerensen.garbagev1.domain

import kotlinx.coroutines.flow.Flow

interface RecyclingStationRepository {
    fun getShops(): Flow<List<Shop>>
}