package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ripple.trackingmaster.devicetrackapp.data.repo.NetworkBeltRepository
import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
import javax.inject.Inject

// This is the new state for the Dashboard UI
data class DashboardUiState(
    val deviceCount: Int = 0,
    val siteCount: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val beltRepository: NetworkBeltRepository, // ✅ USE NEW REPO
    private val siteRepository: SiteRepository         // ✅ USE NEW REPO
) : ViewModel() {

    // Combine the flows from both repositories to create the UI state
    val uiState: StateFlow<DashboardUiState> = combine(
        beltRepository.belts,
        siteRepository.observeSites()
    ) { belts, sites ->
        DashboardUiState(
            deviceCount = belts.size,
            siteCount = sites.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState() // Start with 0 counts
    )

    init {
        // Refresh the belt list when the app starts
        beltRepository.refreshBelts()
    }
}