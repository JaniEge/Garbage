package dk.soerensen.garbagev1.ui.features.recycling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.soerensen.garbagev1.domain.Bin
import dk.soerensen.garbagev1.domain.BinRepository
import dk.soerensen.garbagev1.domain.RecyclingStation
import dk.soerensen.garbagev1.domain.RecyclingStationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import dk.soerensen.garbagev1.data.geofence.GeofenceManager

@HiltViewModel
class RecyclingViewModel @Inject constructor(
    private val recyclingStationRepository: RecyclingStationRepository,
    private val binRepository: BinRepository,
    private val geofenceManager: GeofenceManager
) : ViewModel() {

    // ✅ Henter dine Bins som en Flow direkte fra Firebase
    val bins: Flow<List<Bin>> = binRepository.getBins()

    data class UiState(
        val stations: List<RecyclingStation> = emptyList(),
        val filteredStations: List<RecyclingStation> = emptyList(),
        val availableBinTypes: List<String> = emptyList(),
        val selectedBinFilters: Set<String> = emptySet(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val userLatitude: Double? = null,
        val userLongitude: Double? = null,
        val locationSortingEnabled: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadRecyclingStations()
    }

    fun trackRecycling(bin: Bin) {
        viewModelScope.launch {
            try {
                val updatedBin = bin.copy(
                    lastPickupTime = System.currentTimeMillis(),
                    count = bin.count + 1
                )
                binRepository.updateBin(updatedBin)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Kunne ikke opdatere: ${e.message}") }
            }
        }
    }

    private fun normalizeBinType(binType: String): String =
        binType.trim().lowercase().replaceFirstChar { it.uppercase() }

    fun loadRecyclingStations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            recyclingStationRepository.getRecyclingStations()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { stations ->
                    val binTypes = stations
                        .flatMap { it.bins }
                        .map { normalizeBinType(it) }
                        .distinct()
                        .sorted()
                    _uiState.update {
                        it.copy(
                            stations = stations,
                            filteredStations = stations,
                            availableBinTypes = binTypes,
                            isLoading = false
                        )
                    }
                }
        }
    }
    fun enableGeofencing() {
        geofenceManager.registerGeofences(uiState.value.stations)
    }

    fun setUserLocation(lat: Double, lon: Double) {
        _uiState.update { state ->
            state.copy(
                userLatitude = lat,
                userLongitude = lon,
                locationSortingEnabled = true,
                filteredStations = sortByLocation(state.filteredStations, lat, lon)
            )
        }
    }

    private fun sortByLocation(
        stations: List<RecyclingStation>,
        lat: Double,
        lon: Double
    ): List<RecyclingStation> {
        return stations.sortedBy { station ->
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                lat, lon,
                station.latitude, station.longitude,
                results
            )
            results[0]
        }
    }

    fun toggleBinFilter(binType: String) {
        _uiState.update { state ->
            val newFilters = if (binType in state.selectedBinFilters) {
                state.selectedBinFilters - binType
            } else {
                state.selectedBinFilters + binType
            }
            var filtered = if (newFilters.isEmpty()) {
                state.stations
            } else {
                val normalizedFilters = newFilters.map { it.trim().lowercase() }.toSet()
                state.stations.filter { station ->
                    station.bins.any { bin -> bin.trim().lowercase() in normalizedFilters }
                }
            }
            if (state.locationSortingEnabled && state.userLatitude != null && state.userLongitude != null) {
                filtered = sortByLocation(filtered, state.userLatitude, state.userLongitude)
            }
            state.copy(
                selectedBinFilters = newFilters,
                filteredStations = filtered
            )
        }
    }

    fun clearFilters() {
        _uiState.update { state ->
            var stations = state.stations
            if (state.locationSortingEnabled && state.userLatitude != null && state.userLongitude != null) {
                stations = sortByLocation(stations, state.userLatitude, state.userLongitude)
            }
            state.copy(
                selectedBinFilters = emptySet(),
                filteredStations = stations
            )
        }
    }
}