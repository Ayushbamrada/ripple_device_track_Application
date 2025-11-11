//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
//import javax.inject.Inject
//
//@HiltViewModel
//class SitesViewModel @Inject constructor(
//    private val repo: SiteRepository
//) : ViewModel() {
//
//    val sites = repo.observeSites()
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//    fun createSite(name: String, loc: String?) {
//        viewModelScope.launch {
//            repo.createSite(name, loc)
//        }
//    }
//}
package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
import javax.inject.Inject

data class SiteUiModel(
    val id: Int,
    val siteName: String,
    val location: String?,
    val deviceCount: Int
)

@HiltViewModel
class SitesViewModel @Inject constructor(
    private val repo: SiteRepository
) : ViewModel() {

    val sites: StateFlow<List<SiteUiModel>> =
        repo.observeSites()
            .flatMapLatest { siteList ->
                if (siteList.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(
                        siteList.map { site ->
                            repo.observeDevicesForSite(site.id).map { devices ->
                                SiteUiModel(
                                    id = site.id,
                                    siteName = site.siteName,
                                    location = site.location,
                                    deviceCount = devices.size
                                )
                            }
                        }
                    ) { it.toList() }
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}
