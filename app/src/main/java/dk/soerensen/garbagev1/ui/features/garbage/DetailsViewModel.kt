package dk.soerensen.garbagev1.ui.features.garbage

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.ItemRepository
import dk.soerensen.garbagev1.ui.navigation.GarbageDetailsRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val detailsArgs: GarbageDetailsRoute = savedStateHandle.toRoute()
    private val itemId: String = detailsArgs.itemId

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        Log.d("DetailsVM", "Looking for itemId: '$itemId'")
        viewModelScope.launch {
            itemRepository.getItem(id = itemId).collect { item ->
                Log.d("DetailsVM", "Got item: $item")
                _uiState.update { it.copy(selectedItem = item) }
            }
        }
    }

    val uiEvents: UiEvents = object : UiEvents {

        override fun onNameChange(name: String) {
            _uiState.update { it.copy(selectedItem = it.selectedItem?.copy(name = name)) }
        }

        override fun onBinChange(bin: String) {
            _uiState.update { it.copy(selectedItem = it.selectedItem?.copy(bin = bin)) }
        }

        override fun onSaveClick() {
            _uiState.value.selectedItem?.let { item ->
                viewModelScope.launch {
                    itemRepository.updateItem(item = item)
                }
            }
            onUpClick()
        }

        override fun onDeleteClick() {
            _uiState.update { it.copy(showDeleteDialog = true) }
        }

        override fun onConfirmDeleteClick() {
            _uiState.value.selectedItem?.let { item ->
                viewModelScope.launch {
                    itemRepository.remove(item)
                }
            }
            _uiState.update { it.copy(showDeleteDialog = false) }
            onUpClick()
        }

        override fun onDismissDeleteClick() {
            _uiState.update { it.copy(showDeleteDialog = false) }
        }

        override fun onUpClick() {
            viewModelScope.launch {
                _navigationEvents.emit(NavigationEvent.NavigateUp)
            }
        }
    }

    data class UiState(
        val selectedItem: GarbageItem? = null,
        val showDeleteDialog: Boolean = false,
    )

    @Immutable
    interface UiEvents {
        fun onNameChange(name: String)
        fun onBinChange(bin: String)
        fun onSaveClick()
        fun onDeleteClick()
        fun onConfirmDeleteClick()
        fun onDismissDeleteClick()
        fun onUpClick()
    }

    sealed class NavigationEvent {
        data object NavigateUp : NavigationEvent()
    }
}