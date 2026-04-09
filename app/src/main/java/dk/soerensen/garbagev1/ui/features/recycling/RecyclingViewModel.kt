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
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadRecyclingStations()
    }

    // ✅ Opdaterer lastPickupTime i Firebase når der trykkes på knappen
    fun updateBin(bin: Bin) {
        viewModelScope.launch {
            try {
                binRepository.updateBin(bin)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Kunne ikke opdatere: ${e.message}") }
            }
        }
    }

    fun loadRecyclingStations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            recyclingStationRepository.getRecyclingStations()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { stations ->
                    _uiState.update { it.copy(stations = stations, isLoading = false) }
                }
        }
    }
    fun enableGeofencing() {
        geofenceManager.registerGeofences(uiState.value.stations)
    }
}