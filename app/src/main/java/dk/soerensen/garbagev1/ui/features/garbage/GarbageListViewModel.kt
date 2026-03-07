package dk.soerensen.garbagev1.ui.features.garbage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.soerensen.garbagev1.domain.BinRepository
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.ItemRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GarbageListViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val binRepository: BinRepository,
) : ViewModel() {

    data class UiState(
        val items: List<GarbageItem> = emptyList(),
        val binImageUrls: Map<String, String> = emptyMap(),
    )

    sealed interface NavigationEvent {
        data object NavigateUp : NavigationEvent
        data object NavigateToAdd : NavigationEvent
        data class NavigateToDetails(val itemId: String) : NavigationEvent
    }

    private val _navigationEvents = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    val uiState: StateFlow<UiState> =
        itemRepository.getItems()
            .combine(binRepository.getBins()) { items, bins ->
                val binImageUrls = bins.associate { bin ->
                    bin.id.trim().lowercase() to bin.imageUrl
                }
                UiState(items = items, binImageUrls = binImageUrls)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState()
            )

    fun onEditClicked(item: GarbageItem) {
        viewModelScope.launch {
            _navigationEvents.send(NavigationEvent.NavigateToDetails(item.id))
        }
    }

    fun onAddClicked() {
        viewModelScope.launch {
            _navigationEvents.send(NavigationEvent.NavigateToAdd)
        }
    }

    fun onUpClicked() {
        viewModelScope.launch {
            _navigationEvents.send(NavigationEvent.NavigateUp)
        }
    }
}