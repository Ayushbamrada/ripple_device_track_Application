//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
//import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
//import ripple.trackingmaster.devicetrackapp.data.repo.DeviceRepository
//import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
//import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController
//import javax.inject.Inject
//
//data class DeviceDetailUiState(
//    val mac: String = "",
//    val name: String? = null,
//    val isConnecting: Boolean = false,
//    val isConnected: Boolean = false,
//    val lPitch: Float? = null,
//    val rPitch: Float? = null,
//    val cPitch: Float? = null
//)
//
//@HiltViewModel
//class DeviceDetailViewModel @Inject constructor(
//    private val controller: BluetoothController,
//    private val deviceRepo: DeviceRepository,
//    private val siteRepo: SiteRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(DeviceDetailUiState())
//    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()
//
//    // Expose sites so screens don't touch repositories directly
//    val sites: StateFlow<List<SiteEntity>> =
//        siteRepo.observeSites().stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5_000),
//            emptyList()
//        )
//
//    fun init(mac: String) {
//        _uiState.update { it.copy(mac = mac) }
//    }
//
//    fun connect() {
//        viewModelScope.launch {
//            val mac = uiState.value.mac
//            _uiState.update { it.copy(isConnecting = true) }
//            val ok = controller.connect(mac)
//            _uiState.update { it.copy(isConnecting = false, isConnected = ok) }
//            if (ok) {
//                saveDevice()
//                controller.startStreaming()
//                observeSensor()
//            }
//        }
//    }
//
//    private suspend fun saveDevice() {
//        deviceRepo.saveDevice(
//            DeviceEntity(
//                mac = uiState.value.mac,
//                name = "HipPro Belt"
//            )
//        )
//    }
//
//    fun assignToSite(siteId: Int) {
//        viewModelScope.launch {
//            siteRepo.assignDeviceToSite(uiState.value.mac, siteId)
//        }
//    }
//
//    private fun observeSensor() {
//        viewModelScope.launch {
//            controller.sensorData.collect { data ->
//                if (data != null) {
//                    _uiState.update {
//                        it.copy(
//                            lPitch = data.lPitch,
//                            rPitch = data.rPitch,
//                            cPitch = data.cPitch
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    fun disconnect() {
//        controller.stopStreaming()
//        controller.disconnect()
//        _uiState.update { it.copy(isConnected = false) }
//    }
//}
package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import ripple.trackingmaster.devicetrackapp.data.repo.DeviceRepository
import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController
import javax.inject.Inject

data class DeviceDetailUiState(
    val mac: String = "",
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val lPitch: Float? = null,
    val rPitch: Float? = null,
    val cPitch: Float? = null
)

@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    private val controller: BluetoothController,
    private val repo: DeviceRepository,
    private val siteRepo: SiteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceDetailUiState())
    val uiState = _uiState.asStateFlow()

    val sites: StateFlow<List<SiteEntity>> =
        siteRepo.observeSites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun init(mac: String) {
        _uiState.update { it.copy(mac = mac) }
    }

    fun connect() {
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true) }

            val ok = controller.connect(uiState.value.mac)

            _uiState.update { it.copy(isConnecting = false, isConnected = ok) }

            if (ok) {
                repo.saveDevice(DeviceEntity(mac = uiState.value.mac))
                controller.startStreaming()
                observeSensor()
            }
        }
    }

    private fun observeSensor() {
        viewModelScope.launch {
            controller.sensorData.collect { data ->
                if (data != null) {
                    _uiState.update {
                        it.copy(
                            lPitch = data.lPitch,
                            rPitch = data.rPitch,
                            cPitch = data.cPitch
                        )
                    }
                }
            }
        }
    }

    fun assignToSite(siteId: Int) {
        viewModelScope.launch {
            siteRepo.assignDeviceToSite(uiState.value.mac, siteId)
        }
    }

    fun disconnect() {
        controller.stopStreaming()
        controller.disconnect()
        _uiState.update { it.copy(isConnected = false) }
    }
}

