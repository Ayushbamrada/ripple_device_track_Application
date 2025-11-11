//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import androidx.lifecycle.ViewModel
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import ripple.trackingmaster.devicetrackapp.data.repo.DeviceRepository
//import javax.inject.Inject
//
//data class DashboardStats(
//    val totalDevices: Int = 0
//)
//
//@HiltViewModel
//class DashboardViewModel @Inject constructor(
//    private val repo: DeviceRepository
//) : ViewModel() {
//
//    val stats: StateFlow<DashboardStats> =
//        repo.observeDevices().map { devices ->
//            DashboardStats(
//                totalDevices = devices.size
//            )
//        }.stateIn(
//            started = SharingStarted.WhileSubscribed(5000),
//            scope = kotlinx.coroutines.GlobalScope,
//            initialValue = DashboardStats()
//        )
//}
package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import ripple.trackingmaster.devicetrackapp.data.repo.DeviceRepository
import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
import javax.inject.Inject

data class DashboardStats(
    val totalDevices: Int = 0,
    val assignedDevices: Int = 0, // If you add assignment count later, we’ll compute it here
    val totalSites: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val deviceRepo: DeviceRepository,
    private val siteRepo: SiteRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    init {
        observeStats()
    }

    private fun observeStats() {
        // We only use data that exists in your current schema:
        // - deviceRepo.observeDevices() -> list of DeviceEntity
        // - siteRepo.observeSites()     -> list of SiteEntity
        // You don't have per-device assignment field exposed, so assignedDevices = 0 for now.
        combine(
            deviceRepo.observeDevices(),
            siteRepo.observeSites()
        ) { devices, sites ->
            DashboardStats(
                totalDevices = devices.size,
                assignedDevices = 0,     // update later if you expose assignment info
                totalSites = sites.size
            )
        }.onEach { _stats.value = it }
            .launchIn(viewModelScope)
    }
}
