package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow // Make sure 'flow' is imported
import kotlinx.coroutines.flow.stateIn
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import ripple.trackingmaster.devicetrackapp.data.remote.dto.BeltApiResponse
import ripple.trackingmaster.devicetrackapp.data.repo.NetworkBeltRepository
import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
import javax.inject.Inject

data class SiteDetailUiState(
    val site: SiteEntity? = null,
    val assignedBelts: List<BeltApiResponse> = emptyList()
)

@HiltViewModel
class SiteDetailViewModel @Inject constructor(
    private val siteRepository: SiteRepository,
    private val beltRepository: NetworkBeltRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // --- ▼▼▼ THIS IS THE FIX ▼▼▼ ---
    // We must get the ID as a String and then convert it to an Int.
    private val siteId: Int = savedStateHandle.get<String>("id")?.toIntOrNull() ?: 0
    // --- ▲▲▲ THIS IS THE FIX ▲▲▲ ---

    // Get the flow of all belts
    private val allBeltsFlow = beltRepository.belts

    // Get the specific site
    private val siteFlow = flow { emit(siteRepository.getSiteById(siteId)) }

    // Combine them to create the UI state
    val uiState: StateFlow<SiteDetailUiState> = combine(
        siteFlow,
        allBeltsFlow
    ) { site, allBelts ->
        // Find the site name
        val siteName = site?.siteName ?: ""

        // Filter the list of all belts to find ones assigned to this site
        val assignedBelts = allBelts.filter {
            it.assignedTo.equals(siteName, ignoreCase = true)
        }

        SiteDetailUiState(site, assignedBelts)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SiteDetailUiState()
    )

    init {
        // Make sure we have the latest data
        beltRepository.refreshBelts()
    }
}