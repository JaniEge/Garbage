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
        val hasLocationPermission: Boolean = false,
        val userLocationLat: Double? = null,
        val userLocationLng: Double? = null
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
                    _uiState.update { state ->
                        state.copy(
                            stations = stations,
                            filteredStations = applyFiltersAndSort(
                                stations = stations,
                                filters = state.selectedBinFilters,
                                lat = state.userLocationLat,
                                lng = state.userLocationLng
                            ),
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

    fun updateUserLocation(lat: Double, lng: Double) {
        _uiState.update { state ->
            state.copy(
                hasLocationPermission = true,
                userLocationLat = lat,
                userLocationLng = lng,
                filteredStations = applyFiltersAndSort(
                    stations = state.stations,
                    filters = state.selectedBinFilters,
                    lat = lat,
                    lng = lng
                )
            )
        }
    }

    private fun applyFiltersAndSort(
        stations: List<RecyclingStation>,
        filters: Set<String>,
        lat: Double?,
        lng: Double?
    ): List<RecyclingStation> {
        val filtered = if (filters.isEmpty()) {
            stations
        } else {
            val normalizedFilters = filters.map { it.trim().lowercase() }.toSet()
            stations.filter { station ->
                station.bins.any { bin -> bin.trim().lowercase() in normalizedFilters }
            }
        }
        return if (lat != null && lng != null) {
            filtered.sortedBy { station ->
                val results = FloatArray(1)
                android.location.Location.distanceBetween(lat, lng, station.latitude, station.longitude, results)
                results[0]
            }
        } else {
            filtered
        }
    }

    fun toggleBinFilter(binType: String) {
        _uiState.update { state ->
            val newFilters = if (binType in state.selectedBinFilters) {
                state.selectedBinFilters - binType
            } else {
                state.selectedBinFilters + binType
            }
            state.copy(
                selectedBinFilters = newFilters,
                filteredStations = applyFiltersAndSort(
                    stations = state.stations,
                    filters = newFilters,
                    lat = state.userLocationLat,
                    lng = state.userLocationLng
                )
            )
        }
    }

    fun clearFilters() {
        _uiState.update { state ->
            state.copy(
                selectedBinFilters = emptySet(),
                filteredStations = applyFiltersAndSort(
                    stations = state.stations,
                    filters = emptySet(),
                    lat = state.userLocationLat,
                    lng = state.userLocationLng
                )
            )
        }
    }
}