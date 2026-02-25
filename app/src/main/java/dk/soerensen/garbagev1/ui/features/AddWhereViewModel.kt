package dk.soerensen.garbagev1.ui.features

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.ItemRepository
import dk.soerensen.garbagev1.ui.navigation.AddWhereDialogRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class AddWhereViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args: AddWhereDialogRoute = savedStateHandle.toRoute()
    private val what: String = args.what

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    val uiEvents: UiEvents = object : UiEvents {

        override fun onWhereChange(where: String) {
            _uiState.update { it.copy(where = where, isError = false) }
        }

        override fun onDoneClick() {
            val bin = uiState.value.where.trim()
            val name = what.trim()

            if (name.isNotBlank() && bin.isNotBlank()) {
                // Add new item to the top (index 0)
                itemRepository.add(
                    index = 0,
                    item = GarbageItem(name = name, bin = bin)
                )

                viewModelScope.launch {
                    _navigationEvents.emit(NavigationEvent.CloseDialog)
                }
            } else {
                _uiState.update { it.copy(isError = true) }
            }
        }

        override fun onUpClick() {
            viewModelScope.launch {
                _navigationEvents.emit(NavigationEvent.CloseDialog)
            }
        }

        override fun onCancelClick() {
            viewModelScope.launch {
                _navigationEvents.emit(NavigationEvent.CloseDialog)
            }
        }
    }

    data class UiState(
        val where: String = "",
        val isError: Boolean = false
    )

    @Immutable
    interface UiEvents {
        fun onWhereChange(where: String)
        fun onDoneClick()
        fun onUpClick()
        fun onCancelClick()
    }

    sealed class NavigationEvent {
        data object CloseDialog : NavigationEvent()
    }
}