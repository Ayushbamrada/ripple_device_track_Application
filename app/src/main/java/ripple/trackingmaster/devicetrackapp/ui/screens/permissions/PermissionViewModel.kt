package ripple.trackingmaster.devicetrackapp.ui.screens.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ripple.trackingmaster.devicetrackapp.data.local.PreferenceManager
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val prefs: PreferenceManager
) : ViewModel() {

    val hasPermissions: StateFlow<Boolean> =
        prefs.permissionGranted.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(10_000),
            false
        )

    fun setGranted() {
        viewModelScope.launch {
            prefs.setPermissionGranted(true)
        }
    }
}
