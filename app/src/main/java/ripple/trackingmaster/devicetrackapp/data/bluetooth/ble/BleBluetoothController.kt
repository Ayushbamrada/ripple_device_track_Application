package ripple.trackingmaster.devicetrackapp.data.bluetooth.ble

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
import ripple.trackingmaster.devicetrackapp.domain.model.SensorData
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController

/**
 * BLE placeholder implementation.
 *
 * This class implements the same BluetoothController interface as the Classic (HC-05) controller,
 * so you can swap implementations via DI when you add BLE support later (e.g., for Stride).
 *
 * TODOs when enabling BLE:
 *  - Scan using BluetoothLeScanner with filters
 *  - Connect via BluetoothGatt
 *  - Discover services/characteristics
 *  - Subscribe to notifications for sensor packets
 *  - Parse packets into SensorData (same model as Classic for UI reuse)
 */
class BleBluetoothController : BluetoothController {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _sensorData = MutableStateFlow<SensorData?>(null)
    override val sensorData: StateFlow<SensorData?> = _sensorData

    override suspend fun connect(address: String): Boolean {
        // Stub for now. Return false to indicate not supported yet.
        _connectionState.value = ConnectionState.FAILED
        return false
    }

    override fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override fun startStreaming() {
        // No-op (will be implemented when BLE support is added)
    }

    override fun stopStreaming() {
        // No-op
    }

    override fun sendReset() {
        // No-op (depends on your BLE protocol command)
    }
}
