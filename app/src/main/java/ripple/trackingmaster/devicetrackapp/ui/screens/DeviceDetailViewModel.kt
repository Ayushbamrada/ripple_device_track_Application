package ripple.trackingmaster.devicetrackapp.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import ripple.trackingmaster.devicetrackapp.data.repo.DeviceRepository
import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController
import javax.inject.Inject

data class DeviceDetailUiState(
    val mac: String = "",
    val serialNumber: String? = null,
    val customName: String? = null,
    val beltNumber: String? = null,
    val beltSize: String? = null,
    val lastSeenStatus: String? = null
)

@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    private val controller: BluetoothController,
    private val repo: DeviceRepository,
    private val siteRepo: SiteRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceDetailUiState())
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    val connectionState: StateFlow<ConnectionState> = controller.connectionState

    val sites: StateFlow<List<SiteEntity>> =
        siteRepo.observeSites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveButtonText = MutableStateFlow("Save Details")
    val saveButtonText: StateFlow<String> = _saveButtonText.asStateFlow()

    private val mac: String = savedStateHandle.get("mac") ?: ""

    val assignedSiteId: StateFlow<Int?> = siteRepo.observeSiteIdForDevice(mac)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        _uiState.update { it.copy(mac = mac) }
        loadDeviceDetails()
        observeSerialFlow()
    }

    private fun loadDeviceDetails() {
        viewModelScope.launch {
            val existingDevice = repo.getDevice(mac)
            if (existingDevice != null) {
                _uiState.update {
                    it.copy(
                        serialNumber = existingDevice.serialNumber,
                        customName = existingDevice.customName,
                        beltNumber = existingDevice.beltNumber,
                        beltSize = existingDevice.beltSize,
                        // ▼▼▼ THIS WAS THE TYPO ▼▼▼
                        lastSeenStatus = existingDevice.lastSeenStatus
                    )
                }
            }
        }
    }

    private fun observeSerialFlow() {
        viewModelScope.launch {
            controller.serialFlow.collect { realSerial ->
                Log.d("DeviceDetailVM", "🔐 REAL SERIAL RECEIVED: $realSerial")
                _uiState.update { it.copy(serialNumber = realSerial) }
                saveDeviceDetails()
            }
        }
    }

    // --------------------------------------------------------------
    // CONNECT / DISCONNECT
    // --------------------------------------------------------------
    fun connect() {
        viewModelScope.launch {
            val ok = controller.connect(mac)
            if (ok) {
                _uiState.update { it.copy(lastSeenStatus = "Connected") }
            } else {
                _uiState.update { it.copy(lastSeenStatus = "Connection Failed") }
            }
        }
    }

    fun disconnect() {
        controller.disconnect()
        _uiState.update { it.copy(lastSeenStatus = "Disconnected") }
        saveDeviceDetails()
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

    // --------------------------------------------------------------
    // ASSIGN / UNASSIGN
    // --------------------------------------------------------------
    fun assignToSite(siteId: Int) {
        viewModelScope.launch { siteRepo.assignDeviceToSite(uiState.value.mac, siteId) }
    }

    fun unassignFromSite() {
        viewModelScope.launch { siteRepo.unassignDevice(uiState.value.mac) }
    }

    // --------------------------------------------------------------
    // Editable Fields
    // --------------------------------------------------------------
    fun updateName(name: String) {
        _uiState.update { it.copy(customName = name) }
    }

    fun updateBeltNumber(num: String) {
        _uiState.update { it.copy(beltNumber = num) }
    }

    fun updateBeltSize(size: String) {
        _uiState.update { it.copy(beltSize = size) }
    }

    // --------------------------------------------------------------
    // SAVE DEVICE
    // --------------------------------------------------------------
    fun saveDeviceDetails() {
        if (_saveButtonText.value != "Save Details") {
            return
        }
        _saveButtonText.value = "Saving..."

        viewModelScope.launch {
            val s = uiState.value
            val entity = DeviceEntity(
                mac = s.mac,
                serialNumber = s.serialNumber,
                customName = s.customName ?: "Hip-Pro",
                beltNumber = s.beltNumber,
                beltSize = s.beltSize,
                lastSeenStatus = s.lastSeenStatus
            )

            repo.saveDevice(entity)
            Log.d("DeviceDetailVM", "💾 Device saved: $entity")

            _saveButtonText.value = "Saved!"
            delay(2000)
            _saveButtonText.value = "Save Details"
        }
    }
}