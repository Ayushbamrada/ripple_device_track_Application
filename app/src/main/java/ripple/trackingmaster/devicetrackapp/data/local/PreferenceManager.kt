package ripple.trackingmaster.devicetrackapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("hippro_prefs")

class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val PERMISSION_GRANTED = booleanPreferencesKey("permission_granted")
    }

    val permissionGranted = context.dataStore.data.map { prefs ->
        prefs[PERMISSION_GRANTED] ?: false
    }

    suspend fun setPermissionGranted(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PERMISSION_GRANTED] = value
        }
    }
}
