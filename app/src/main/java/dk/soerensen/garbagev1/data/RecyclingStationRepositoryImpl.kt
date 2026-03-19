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
        val response = apiService.getRecyclingStations()

        val stations = response.features.map { feature ->
            val properties = feature.properties
            val firstCoordinatePair = feature.geometry.coordinates.firstOrNull()

            val longitude = firstCoordinatePair?.getOrNull(0) ?: 0.0
            val latitude = firstCoordinatePair?.getOrNull(1) ?: 0.0

            RecyclingStation(
                id = properties.id.toString(),
                name = properties.navn ?: "",
                category = properties.kategori ?: "",
                address = properties.adresse ?: "",
                status = properties.status ?: "",
                bins = properties.fraktioner
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotBlank() }
                    ?: emptyList(),
                latitude = latitude,
                longitude = longitude
            )
        }

        emit(stations)
    }
}