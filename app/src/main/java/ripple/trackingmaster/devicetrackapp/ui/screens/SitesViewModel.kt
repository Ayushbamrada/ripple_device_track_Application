package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import ripple.trackingmaster.devicetrackapp.data.repo.NetworkBeltRepository
import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
import javax.inject.Inject

// A new UI model to hold the site and its device count
data class SiteWithDeviceCount(
    val site: SiteEntity,
    val deviceCount: Int
)

@HiltViewModel
class SitesViewModel @Inject constructor(
    private val siteRepository: SiteRepository,
    private val beltRepository: NetworkBeltRepository
) : ViewModel() {

    // Get the flow of all sites from the local DB
    private val sitesFlow = siteRepository.observeSites()

    // Get the flow of all belts from the network
    private val beltsFlow = beltRepository.belts

    // Combine them to create the UI state
    val sitesWithCount: StateFlow<List<SiteWithDeviceCount>> = combine(
        sitesFlow,
        beltsFlow
    ) { sites, belts ->
        // Create a map of site name -> device count
        val deviceCountBySite = belts
            .filter { !it.assignedTo.isNullOrEmpty() }
            .groupBy { it.assignedTo!! }
            .mapValues { it.value.size }

        // Map each SiteEntity to our new UI model
        sites.map { site ->
            val count = deviceCountBySite[site.siteName] ?: 0
            SiteWithDeviceCount(site = site, deviceCount = count)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Ensure the belt list is fresh when this VM is created
        beltRepository.refreshBelts()
    }
}