package ripple.trackingmaster.devicetrackapp.data.bluetooth.ble

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController
import javax.inject.Inject

class BleBluetoothController @Inject constructor() : BluetoothController {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableStateFlow("")
    override val incomingMessages: StateFlow<String> = _incomingMessages.asStateFlow()

    override fun connect(macAddress: String) {
        Log.d("BleBT", "BLE connect called for $macAddress (Stub)")
        _connectionState.value = ConnectionState.CONNECTED
        _incomingMessages.value = "SN:BLE-STUB-123"
    }

    override fun disconnect() {
        Log.d("BleBT", "BLE disconnect called")
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override fun sendCommand(command: String) {
        Log.d("BleBT", "BLE send command: $command")
    }
}