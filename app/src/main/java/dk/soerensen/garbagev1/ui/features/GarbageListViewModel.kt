package dk.soerensen.garbagev1.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.ItemRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GarbageListViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    data class UiState(
        val items: List<GarbageItem> = emptyList(),
        val selectedItemId: String? = null
    )

    sealed interface UiEffect {
        data class ShowUndo(val itemName: String) : UiEffect
    }

    sealed interface NavigationEvent {
        data object NavigateUp : NavigationEvent
        data object NavigateToAdd : NavigationEvent
        data class NavigateToDetails(val itemId: String) : NavigationEvent
    }

    private val _effects = Channel<UiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val _navigationEvents = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    private var lastDeleted: Pair<Int, GarbageItem>? = null

    private val selectedId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<UiState> =
        kotlinx.coroutines.flow.combine(repository.items, selectedId) { items, selId ->
            UiState(items = items, selectedItemId = selId)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState()
        )

    // Screen bruger den til "isSelected"
    fun selected(): StateFlow<GarbageItem?> {
        val out = MutableStateFlow<GarbageItem?>(null)

        // holder den opdateret
        viewModelScope.launch {
            uiState.collect { state ->
                val item = state.items.firstOrNull { it.id == state.selectedItemId }
                out.value = item
            }
        }
        return out.asStateFlow()
    }

    fun onItemClicked(item: GarbageItem) {
        selectedId.update { current ->
            if (current == item.id) null else item.id
        }
    }

    fun onEditClicked(item: GarbageItem) {
        viewModelScope.launch {
            _navigationEvents.send(NavigationEvent.NavigateToDetails(item.id))
        }
    }

    fun onDeleteClicked(item: GarbageItem) {
        val index = repository.remove(item)
        if (index >= 0) {
            lastDeleted = index to item
            selectedId.value = null
            viewModelScope.launch { _effects.send(UiEffect.ShowUndo(itemName = item.name)) }
        }
    }

    fun onUndoDelete() {
        lastDeleted?.let { (index, item) ->
            repository.add(index, item)
        }
        lastDeleted = null
    }

    fun onAddClicked() {
        viewModelScope.launch { _navigationEvents.send(NavigationEvent.NavigateToAdd) }
    }

    fun onUpClicked() {
        viewModelScope.launch { _navigationEvents.send(NavigationEvent.NavigateUp) }
    }
}