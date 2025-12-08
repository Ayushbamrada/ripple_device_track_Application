package ripple.trackingmaster.devicetrackapp.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState

interface BluetoothController {
    val connectionState: StateFlow<ConnectionState>
    val incomingMessages: StateFlow<String>

    fun connect(macAddress: String)
    fun disconnect()
    fun sendCommand(command: String) // ✅ Re-added
}