package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import ripple.trackingmaster.devicetrackapp.data.remote.dto.BeltApiResponse
import ripple.trackingmaster.devicetrackapp.data.repo.DeviceControlRepository
import ripple.trackingmaster.devicetrackapp.data.repo.NetworkBeltRepository
import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
import javax.inject.Inject

@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    private val deviceRepository: DeviceControlRepository,
    private val siteRepository: SiteRepository,
    private val networkRepository: NetworkBeltRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val macAddress: String = savedStateHandle.get<String>("mac") ?: ""

    // Form State
    private val _beltSize = MutableStateFlow("")
    private val _teamMember = MutableStateFlow("")
    private val _assignedSite = MutableStateFlow<SiteEntity?>(null)

    private val _saveState = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    private var lastKnownSerial = "Unknown"

    sealed class SaveStatus {
        object Idle : SaveStatus()
        object Saving : SaveStatus()
        data class Success(val message: String) : SaveStatus()
        data class Error(val message: String) : SaveStatus()
    }

    val uiState: StateFlow<DeviceDetailUiState> = combine(
        deviceRepository.serialNumber,
        deviceRepository.connectionState,
        _beltSize,
        _teamMember,
        _assignedSite,
        siteRepository.observeSites(),
        _saveState
    ) { args ->
        val incomingSerial = args[0] as String
        val connState = args[1] as ConnectionState
        val belt = args[2] as String
        val member = args[3] as String
        val site = args[4] as? SiteEntity
        @Suppress("UNCHECKED_CAST")
        val sites = args[5] as List<SiteEntity>
        val saveStatus = args[6] as SaveStatus

        if (incomingSerial != "Unknown" && incomingSerial.isNotBlank()) {
            lastKnownSerial = incomingSerial
        }

        DeviceDetailUiState(
            macAddress = macAddress,
            serialNumber = lastKnownSerial,
            connectionState = connState,
            beltSize = belt,
            teamMember = member,
            assignedSite = site,
            availableSites = sites,
            // You can also load these options from a DB/API if needed
            teamMemberOptions = listOf("Ayush", "Mukul", "Happy", "Abhi"),
            isSaving = saveStatus is SaveStatus.Saving,
            saveMessage = (saveStatus as? SaveStatus.Success)?.message,
            errorMessage = (saveStatus as? SaveStatus.Error)?.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeviceDetailUiState(macAddress = macAddress)
    )

    init {
        connect()
        // ✅ NEW: Pre-fill data if it exists in the database
        loadExistingData()
    }

    private fun loadExistingData() {
        viewModelScope.launch {
            // 1. Get the belt from DB
            val savedBelt = networkRepository.getBeltByMac(macAddress)

            if (savedBelt != null) {
                // 2. Pre-fill text fields
                _beltSize.value = savedBelt.beltSize ?: ""
                _teamMember.value = savedBelt.teamMember ?: ""

                // 3. Pre-fill Site (We have to find the SiteEntity by name)
                if (!savedBelt.assignedTo.isNullOrEmpty()) {
                    // We get the list of sites currently available to find the matching object
                    val allSites = siteRepository.observeSites().first()
                    val matchingSite = allSites.find { it.siteName == savedBelt.assignedTo }
                    _assignedSite.value = matchingSite
                }
            }
        }
    }

    fun connect() {
        if (macAddress.isNotBlank()) {
            viewModelScope.launch { deviceRepository.connect(macAddress) }
        }
    }

    fun disconnect() {
        viewModelScope.launch { deviceRepository.disconnect() }
    }

    fun fetchSerialNumber() {
        // Updated command from your Flutter code
        val command = "aa1955000000000000000000"
        viewModelScope.launch {
            deviceRepository.sendCommand(command)
        }
    }

    fun updateBeltSize(size: String) { _beltSize.value = size }
    fun updateTeamMember(name: String) { _teamMember.value = name }
    fun assignSite(site: SiteEntity) { _assignedSite.value = site }
    fun clearMessages() { _saveState.value = SaveStatus.Idle }

    fun saveDetails() {
        val currentState = uiState.value

        if (currentState.beltSize.isBlank()) {
            _saveState.value = SaveStatus.Error("Please enter Belt Size")
            return
        }
        if (currentState.teamMember.isBlank()) {
            _saveState.value = SaveStatus.Error("Please select a Team Member")
            return
        }

        viewModelScope.launch {
            _saveState.value = SaveStatus.Saving
            try {
                val beltData = BeltApiResponse(
                    id = 0,
                    macAddress = macAddress,
                    serialNumber = if (lastKnownSerial == "Unknown") null else lastKnownSerial,
                    beltSize = currentState.beltSize,
                    teamMember = currentState.teamMember,
                    assignedTo = currentState.assignedSite?.siteName,
                    createdAt = "",
                    updatedAt = ""
                )

                networkRepository.syncBelt(beltData)

                _saveState.value = SaveStatus.Success("Device saved successfully!")
            } catch (e: Exception) {
                _saveState.value = SaveStatus.Error(e.message ?: "Failed to save data")
            }
        }
    }
}