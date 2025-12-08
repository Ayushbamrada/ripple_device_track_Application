package ripple.trackingmaster.devicetrackapp.data.repo

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceControlRepository @Inject constructor(
    private val controller: BluetoothController
) {
    val connectionState: Flow<ConnectionState> = controller.connectionState
    val incomingMessages: Flow<String> = controller.incomingMessages

    // ✅ FIXED: Parse raw HEX string into readable Serial Number
    val serialNumber: Flow<String> = controller.incomingMessages.map { rawHex ->
        if (rawHex.isBlank() || rawHex.length < 22) {
            "Unknown"
        } else {
            try {
                // Remove the command echo "aa1955" if it's at the end
                val cleanHex = rawHex.replace("aa1955", "")

                if (cleanHex.length >= 22) {
                    parseSerialNumber(cleanHex)
                } else {
                    "Invalid Data"
                }
            } catch (e: Exception) {
                Log.e("Repo", "Parsing failed: ${e.message}")
                "Error"
            }
        }
    }

    private fun parseSerialNumber(hex: String): String {
        // Based on Flutter logic:
        // RHPLHPV1.0 + Day(2) + Month(2) + MAC(12) + Size(2) + Number(4)

        try {
            val dayHex = hex.substring(0, 2)
            val monthHex = hex.substring(2, 4)
            val macHex = hex.substring(4, 16).uppercase()
            val sizeHex = hex.substring(16, 18)
            val numberHex = hex.substring(18, 22)

            val day = dayHex.toInt(16).toString().padStart(2, '0')
            val month = monthHex.toInt(16).toString().padStart(2, '0')
            val size = sizeHex.toInt(16).toString()
            val number = numberHex.toInt(16).toString().padStart(4, '0')

            return "RHPLHPV1.0$day$month$macHex$size$number"
        } catch (e: Exception) {
            return "Parse Error"
        }
    }

    fun connect(macAddress: String) = controller.connect(macAddress)
    fun disconnect() = controller.disconnect()
    fun sendCommand(cmd: String) = controller.sendCommand(cmd)
}