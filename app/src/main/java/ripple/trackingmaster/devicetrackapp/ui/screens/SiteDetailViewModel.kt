//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
//import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
//import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
//import javax.inject.Inject
//
//@HiltViewModel
//class SiteDetailViewModel @Inject constructor(
//    private val repo: SiteRepository
//) : ViewModel() {
//
//    private val _site = MutableStateFlow<SiteEntity?>(null)
//    val site = _site.asStateFlow()
//
//    private val _devices = MutableStateFlow<List<DeviceEntity>>(emptyList())
//    val devices = _devices.asStateFlow()
//
//    fun load(siteId: Int) {
//        viewModelScope.launch {
//            _site.value = repo.getSiteById(siteId)
//            // Keep collecting devices for this site
//            repo.observeDevicesForSite(siteId).collect { list ->
//                _devices.value = list
//            }
//        }
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
import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
import javax.inject.Inject

data class SiteDetailUiState(
    val site: SiteEntity? = null,
    val devices: List<DeviceEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class SiteDetailViewModel @Inject constructor(
    private val repo: SiteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SiteDetailUiState())
    val uiState: StateFlow<SiteDetailUiState> = _uiState.asStateFlow()

    fun load(siteId: Int) {
        viewModelScope.launch {

            // 1) Load site info
            val site = repo.getSiteById(siteId)

            // 2) Observe devices assigned to site
            val deviceFlow = repo.observeDevicesForSite(siteId)

            deviceFlow.collect { list ->
                _uiState.update {
                    it.copy(
                        site = site,
                        devices = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun unassign(mac: String) {
        viewModelScope.launch {
            repo.unassignDevice(mac)
        }
    }
}
