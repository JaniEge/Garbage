package dk.soerensen.garbagev1.ui.features

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.ItemRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GarbageViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val query: String = "",
        val result: String = "",
        val showList: Boolean = false,
        val items: List<GarbageItem> = emptyList(),
        val selectedItem: GarbageItem? = null
    )

    sealed interface UiEffect {
        data class ShowSnackbar(
            val message: String,
            val actionLabel: String?
        ) : UiEffect
    }

    private val _effects = Channel<UiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var lastDeleted: Pair<Int, GarbageItem>? = null

    private val _state = MutableStateFlow(
        UiState(query = savedStateHandle["query"] ?: "")
    )

    val uiState: StateFlow<UiState> =
        combine(_state, repository.items) { state, items ->
            state.copy(items = items)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onQueryChanged(value: String) {
        savedStateHandle["query"] = value
        _state.update { it.copy(query = value) }
    }

    fun onWhereClicked() {
        val q = uiState.value.query.trim()
        val bin = repository.findBin(q)

        val resultText =
            if (bin != null) "$q should be placed in: $bin"
            else "Item not found"

        _state.update { it.copy(result = resultText) }
    }

    fun onListClicked() {
        _state.update { it.copy(showList = true, selectedItem = null) }
    }

    fun onBackClicked() {
        _state.update { it.copy(showList = false, selectedItem = null) }
    }

    fun onItemClicked(item: GarbageItem) {
        _state.update { state ->
            state.copy(
                selectedItem = if (state.selectedItem == item) null else item
            )
        }
    }

    fun onDeleteClicked(item: GarbageItem) {
        val index = repository.remove(item)
        if (index >= 0) {
            lastDeleted = index to item
            _state.update { it.copy(selectedItem = null) }

            viewModelScope.launch {
                _effects.send(
                    UiEffect.ShowSnackbar(
                        message = " ${item.name}",
                        actionLabel = "Undo"
                    )
                )
            }
        }
    }

    fun onUndoDelete() {
        lastDeleted?.let { (index, item) ->
            repository.add(index, item)
        }
        lastDeleted = null
    }
}
