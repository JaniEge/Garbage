package dk.soerensen.garbagev1.ui.features.recycling

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.soerensen.garbagev1.domain.Bin
import dk.soerensen.garbagev1.domain.BinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RecyclingViewModel @Inject constructor(
    private val binRepository: BinRepository
) : ViewModel() {



    private val _bins = MutableStateFlow<List<Bin>>(emptyList())
    val bins: StateFlow<List<Bin>> = _bins.asStateFlow()

    init {
        // Hvis getBins() er synkron (returnerer List<Bin>)
        _bins.value = binRepository.getBins()
    }
}