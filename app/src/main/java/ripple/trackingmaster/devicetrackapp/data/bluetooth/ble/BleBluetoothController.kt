package ripple.trackingmaster.devicetrackapp.data.bluetooth.ble

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
// import ripple.trackingmaster.devicetrackapp.domain.model.SensorData <-- DELETE THIS
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController

class BleBluetoothController : BluetoothController {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    // --- DELETE SENSOR DATA ---
    // private val _sensorData = MutableStateFlow<SensorData?>(null)
    // override val sensorData: StateFlow<SensorData?> = _sensorData
    // --- END DELETE ---

    private val _serialFlow = MutableSharedFlow<String>()
    override val serialFlow: SharedFlow<String> = _serialFlow

    override suspend fun connect(address: String): Boolean {
        Log.d("BleBT", "BLE connect called (not implemented yet)")
        _connectionState.value = ConnectionState.CONNECTED
        return true
    }

    override fun disconnect() {
        Log.d("BleBT", "BLE disconnect called")
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override fun startStreaming() {
        Log.d("BleBT", "BLE start streaming (stub)")
    }

    override fun stopStreaming() {
        Log.d("BleBT", "BLE stop streaming (stub)")
    }

    override fun sendReset() {
        Log.d("BleBT", "BLE send reset (stub)")
    }
}