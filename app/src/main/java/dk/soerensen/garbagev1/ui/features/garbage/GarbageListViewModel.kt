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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

@HiltViewModel
class GarbageListViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val binRepository: BinRepository,
) : ViewModel() {


    private val binImageUrls: Map<String, String> =
        binRepository.getBins().associate { bin ->
            bin.id.trim().lowercase() to bin.imageUrl
        }

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
        itemRepository.items
            .combine(flowOf(binImageUrls)) { items, urls ->
                UiState(items = items, binImageUrls = urls)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                UiState(binImageUrls = binImageUrls)
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