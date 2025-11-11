package ripple.trackingmaster.devicetrackapp.domain.repository

import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
import ripple.trackingmaster.devicetrackapp.domain.model.SensorData
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {

    val connectionState: StateFlow<ConnectionState>
    val sensorData: StateFlow<SensorData?>

    suspend fun connect(address: String): Boolean
    fun disconnect()

    fun startStreaming()
    fun stopStreaming()

    fun sendReset()
}
