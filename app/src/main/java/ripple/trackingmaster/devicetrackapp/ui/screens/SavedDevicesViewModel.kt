package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ripple.trackingmaster.devicetrackapp.data.remote.dto.BeltApiResponse
import ripple.trackingmaster.devicetrackapp.data.repo.NetworkBeltRepository
import javax.inject.Inject

@HiltViewModel
class SavedDevicesViewModel @Inject constructor(
    private val repository: NetworkBeltRepository
) : ViewModel() {

    // ✅ FIXED: Convert the Flow from Repository to StateFlow
    val belts: StateFlow<List<BeltApiResponse>> = repository.belts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // The screen will show empty list initially while loading DB
        )

    init {
        // Ensure the data is fresh when the screen is loaded
        repository.refreshBelts()
    }
}