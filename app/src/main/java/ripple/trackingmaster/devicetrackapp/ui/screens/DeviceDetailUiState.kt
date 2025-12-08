package ripple.trackingmaster.devicetrackapp.ui.screens

import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState

data class DeviceDetailUiState(
    val macAddress: String = "",
    val serialNumber: String = "Unknown",
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,

    // Form Data
    val beltSize: String = "",
    val teamMember: String = "",
    val assignedSite: SiteEntity? = null, // Store the full Site object

    // Lists
    val availableSites: List<SiteEntity> = emptyList(),
    val teamMemberOptions: List<String> = listOf("Ayush", "Rahul", "Suresh", "Rohan"), // ✅ Fixed List

    // Save Status
    val isSaving: Boolean = false,
    val saveMessage: String? = null, // For Success Toast
    val errorMessage: String? = null // For Error Dialog/Snackbar
)