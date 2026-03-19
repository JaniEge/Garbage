package dk.soerensen.garbagev1.domain

import kotlinx.coroutines.flow.Flow

interface RecyclingStationRepository {
    fun getRecyclingStations(): Flow<List<RecyclingStation>>
}