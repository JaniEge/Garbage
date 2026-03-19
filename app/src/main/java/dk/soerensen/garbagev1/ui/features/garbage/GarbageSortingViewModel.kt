package dk.soerensen.garbagev1.ui.features.garbage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.soerensen.garbagev1.domain.ItemRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GarbageSortingViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val query: String = "",
        val result: String = ""
    )

    sealed interface NavigationEvent {
        data object NavigateToList : NavigationEvent
        data object NavigateToAffaldKbh : NavigationEvent
    }

    private val _navigationEvents = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    private val _uiState = MutableStateFlow(
        UiState(query = savedStateHandle["query"] ?: "")
    )
    val uiState: StateFlow<UiState> = _uiState

    fun onQueryChanged(value: String) {
        savedStateHandle["query"] = value
        _uiState.update { it.copy(query = value) }
    }

    fun onWhereClicked() {
        val q = uiState.value.query.trim()
        viewModelScope.launch {
            val bin = repository.findBin(q)
            val resultText = if (bin != null) {
                "$q should be placed in: $bin"
            } else {
                "Item not found"
            }
            savedStateHandle["query"] = ""
            _uiState.update { it.copy(query = "", result = resultText) }
        }
    }

    fun onListClicked() {
        viewModelScope.launch {
            _navigationEvents.send(NavigationEvent.NavigateToList)
        }
    }

    fun onAffaldKbhClicked() {
        viewModelScope.launch {
            _navigationEvents.send(NavigationEvent.NavigateToAffaldKbh)
        }
    }
}