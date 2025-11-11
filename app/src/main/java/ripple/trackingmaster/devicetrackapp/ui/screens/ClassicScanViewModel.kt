package ripple.trackingmaster.devicetrackapp.ui.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SimpleDevice(
    val name: String?,
    val mac: String
)

@HiltViewModel
class ClassicScanViewModel @Inject constructor(
    private val adapter: BluetoothAdapter
) : ViewModel() {

    private val _devices = MutableStateFlow<List<SimpleDevice>>(emptyList())
    val devices: StateFlow<List<SimpleDevice>> = _devices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // ✅ Start scanning
    @SuppressLint("MissingPermission")
    fun startScan() {
        viewModelScope.launch {

            _devices.value = emptyList()
            _isScanning.value = true

            // ✅ Add paired devices
            val paired = adapter.bondedDevices?.map {
                SimpleDevice(it.name, it.address)
            } ?: emptyList()

            _devices.value = paired

            // ✅ Start discovery
            if (adapter.isDiscovering) adapter.cancelDiscovery()
            adapter.startDiscovery()
        }
    }

    // ✅ Called from MainActivity on ACTION_FOUND
    fun onDeviceFound(name: String?, mac: String) {
        val list = _devices.value.toMutableList()

        if (list.none { it.mac == mac }) {
            list.add(SimpleDevice(name, mac))
        }

        _devices.value = list
    }

    // ✅ Called from MainActivity when ACTION_DISCOVERY_FINISHED
    fun onScanFinished() {
        _isScanning.value = false
    }

    // ✅ Optional stop function
    @SuppressLint("MissingPermission")
    fun stopScan() {
        adapter.cancelDiscovery()
        _isScanning.value = false
    }
}
