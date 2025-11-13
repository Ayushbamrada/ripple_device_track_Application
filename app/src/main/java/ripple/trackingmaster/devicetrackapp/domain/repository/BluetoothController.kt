package ripple.trackingmaster.devicetrackapp.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
import ripple.trackingmaster.devicetrackapp.domain.model.SensorData

interface BluetoothController {

    val connectionState: StateFlow<ConnectionState>
//    val sensorData: StateFlow<SensorData?>
    val serialFlow: SharedFlow<String> // 🔹 emits parsed serial numbers

    suspend fun connect(address: String): Boolean
    fun disconnect()

    fun startStreaming()
    fun stopStreaming()

    fun sendReset()
}
