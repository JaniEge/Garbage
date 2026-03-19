package dk.soerensen.garbagev1.ui.features.recycling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.soerensen.garbagev1.domain.RecyclingStation
import dk.soerensen.garbagev1.domain.RecyclingStationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecyclingViewModel @Inject constructor(
    private val recyclingStationRepository: RecyclingStationRepository
) : ViewModel() {

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

    fun loadRecyclingStations() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            recyclingStationRepository
                .getRecyclingStations()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load recycling stations"
                        )
                    }
                }
                .collect { stations ->
                    _uiState.update {
                        it.copy(
                            stations = stations,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }
}