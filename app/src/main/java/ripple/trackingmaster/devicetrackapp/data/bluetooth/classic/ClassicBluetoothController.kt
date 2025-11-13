package ripple.trackingmaster.devicetrackapp.data.bluetooth.classic

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
// import ripple.trackingmaster.devicetrackapp.domain.model.SensorData  <-- DELETE THIS
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController
import java.io.BufferedInputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*

class ClassicBluetoothController(
    private val adapter: BluetoothAdapter
) : BluetoothController {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var socket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null

    private val sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    // --- REMOVED SENSOR DATA ---
    // private val _sensorData = MutableStateFlow<SensorData?>(null)
    // override val sensorData: StateFlow<SensorData?> = _sensorData

    private val _serialFlow = MutableSharedFlow<String>(replay = 1)
    override val serialFlow: SharedFlow<String> = _serialFlow

    private var deviceStatusFull = StringBuilder()

    @SuppressLint("MissingPermission")
    override suspend fun connect(address: String): Boolean = withContext(Dispatchers.IO) {
        try {
            _connectionState.value = ConnectionState.CONNECTING
            device = adapter.getRemoteDevice(address)
            adapter.cancelDiscovery()
            socket?.close()
            val tmpSocket = device!!.createRfcommSocketToServiceRecord(sppUuid)
            socket = tmpSocket
            tmpSocket.connect()
            _connectionState.value = ConnectionState.CONNECTED
            Log.d("ClassicBT", "✅ Connected successfully")

            startReadingSerial()
            sendReset()

            true
        } catch (e: Exception) {
            Log.e("ClassicBT", "❌ Connection failed: ${e.message}")
            _connectionState.value = ConnectionState.FAILED
            false
        }
    }

    override fun disconnect() {
        try {
            socket?.close()
        } catch (_: IOException) {}
        socket = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override fun sendReset() {
        scope.launch {
            try {
                socket?.outputStream?.write(byteArrayOf(0xAA.toByte(), 0xAA.toByte(), 0x55, 0, 0, 0, 0))
                Log.d("ClassicBT", "✅ Reset command sent")
            } catch (e: Exception) {
                Log.e("ClassicBT", "❌ Reset send failed: ${e.message}")
            }
        }
    }

    /**
     * Parses the 38-char hex string into an ASCII Serial Number.
     * @param serialHex The 38-char hex data *after* the header.
     */
    private fun parseAndEmitSerial(serialHex: String) {
        scope.launch {
            try {
                val serialAscii = serialHex.chunked(2)
                    .map { it.toInt(16).toChar() }
                    .joinToString("")
                    .replace(Regex("[^A-Za-z0-9]"), "") // Clean up any null/non-printable chars

                Log.d("ClassicBT", "✅✅✅ Parsed Serial: $serialAscii")
                _serialFlow.emit(serialAscii)

            } catch (e: Exception) {
                Log.e("ClassicBT", "Serial parse failed: ${e.message}", e)
            }
        }
    }

    // --- REMOVED parseAndEmitSensorData and hexToFloat ---

    private fun startReadingSerial() {
        scope.launch(Dispatchers.IO) {
            val input = socket?.inputStream ?: return@launch
            val bufferedInput = BufferedInputStream(input, 1024)

            // We now look for "58fc41" EXACTLY, as you discovered.
            val headerPattern = Regex("58fc41")
            val headerLength = 6
            val dataLength = 38 // The 19-byte serial number (38 hex chars)
            val totalPacketLength = headerLength + dataLength // 44 chars

            val tempBuffer = ByteArray(1024)

            Log.d("ClassicBT", "📡 Listening for serial packets...")

            while (isActive && connectionState.value == ConnectionState.CONNECTED) {
                try {
                    val bytesRead = bufferedInput.read(tempBuffer)
                    if (bytesRead == -1) throw IOException("Socket closed")

                    if (bytesRead > 0) {
                        // Append the new hex chunk to our buffer
                        val hexChunk = tempBuffer.take(bytesRead)
                            .joinToString("") { String.format("%02x", it) }
                        deviceStatusFull.append(hexChunk)
                        Log.d("ClassicBT", "Read Hex: $hexChunk")

                        if (deviceStatusFull.length > 256) {
                            deviceStatusFull = StringBuilder(deviceStatusFull.takeLast(256))
                        }

                        // Check for a header
                        val headerMatch = headerPattern.find(deviceStatusFull)
                        if (headerMatch != null) {
                            val startIndex = headerMatch.range.first

                            // Check if we have the full packet
                            if (deviceStatusFull.length >= startIndex + totalPacketLength) {

                                // Extract just the 38-char data part
                                val dataStartIndex = startIndex + headerLength
                                val dataEndIndex = dataStartIndex + dataLength
                                val serialHexData = deviceStatusFull.substring(dataStartIndex, dataEndIndex)

                                // Send it to the serial parser
                                parseAndEmitSerial(serialHexData)

                                // Clear the processed packet from the buffer
                                deviceStatusFull = StringBuilder(deviceStatusFull.substring(dataEndIndex))
                            }
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    Log.w("ClassicBT", "Timeout: ${e.message}")
                } catch (e: Exception) {
                    Log.e("ClassicBT", "Read Error, stopping listener: ${e.message}")
                    disconnect()
                    break
                }
            }
        }
    }

    override fun startStreaming() {
        Log.d("ClassicBT", "Start streaming called (doing nothing).")
    }

    override fun stopStreaming() {
        Log.d("ClassicBT", "Stop streaming called (doing nothing).")
    }
}