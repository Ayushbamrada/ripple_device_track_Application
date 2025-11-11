package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
import ripple.trackingmaster.devicetrackapp.data.repo.DeviceRepository
import javax.inject.Inject

@HiltViewModel
class SavedDevicesViewModel @Inject constructor(
    private val repo: DeviceRepository
) : ViewModel() {

    val devices: StateFlow<List<DeviceEntity>> =
        repo.observeDevices().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}
