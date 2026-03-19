package dk.soerensen.garbagev1.data

import dk.soerensen.garbagev1.data.remote.RecyclingStationApiService
import dk.soerensen.garbagev1.domain.RecyclingStation
import dk.soerensen.garbagev1.domain.RecyclingStationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecyclingStationRepositoryImpl @Inject constructor(
    private val apiService: RecyclingStationApiService
) : RecyclingStationRepository {

    override fun getRecyclingStations(): Flow<List<RecyclingStation>> = flow {
        val stations = apiService.getRecyclingStations().map { station ->
            RecyclingStation(
                id = station.id ?: "",
                name = station.navn ?: "",
                category = station.kategori ?: "",
                address = station.adresse ?: "",
                status = station.status ?: "",
                bins = station.fraktioner,
                latitude = station.latitude ?: 0.0,
                longitude = station.longitude ?: 0.0
            )
        }

        emit(stations)
    }
}