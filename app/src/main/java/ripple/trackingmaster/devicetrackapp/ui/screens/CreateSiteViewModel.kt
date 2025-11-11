package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ripple.trackingmaster.devicetrackapp.data.repo.SiteRepository
import javax.inject.Inject

@HiltViewModel
class CreateSiteViewModel @Inject constructor(
    private val repo: SiteRepository
) : ViewModel() {

    fun createSite(name: String, location: String?) {
        viewModelScope.launch {
            repo.createSite(name, location)
        }
    }
}
