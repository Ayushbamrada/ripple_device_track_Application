package ripple.trackingmaster.devicetrackapp.data.bluetooth.classic

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
import ripple.trackingmaster.devicetrackapp.domain.model.SensorData
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.UUID

class ClassicBluetoothController(
    private val adapter: BluetoothAdapter
) : BluetoothController {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var socket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null

    private val sppUuid: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // HC-05 SPP UUID

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _sensorData = MutableStateFlow<SensorData?>(null)
    override val sensorData: StateFlow<SensorData?> = _sensorData

    // ------------------------------------------------------------
    // CONNECT
    // ------------------------------------------------------------
    @SuppressLint("MissingPermission")
    override suspend fun connect(address: String): Boolean = withContext(Dispatchers.IO) {
        try {
            _connectionState.value = ConnectionState.CONNECTING

            device = adapter.getRemoteDevice(address)
            adapter.cancelDiscovery()

            // Close old socket if exists
            socket?.close()

            val tmpSocket = device!!.createRfcommSocketToServiceRecord(sppUuid)
            socket = tmpSocket

            tmpSocket.connect()

            _connectionState.value = ConnectionState.CONNECTED
            Log.d("ClassicBT", "✅ Connected successfully")
            true
        } catch (e: Exception) {
            Log.e("ClassicBT", "❌ Connection failed: ${e.message}")
            _connectionState.value = ConnectionState.FAILED
            false
        }
    }

    // ------------------------------------------------------------
    // DISCONNECT
    // ------------------------------------------------------------
    override fun disconnect() {
        try {
            socket?.close()
        } catch (_: IOException) {}

        socket = null
        _connectionState.value = ConnectionState.DISCONNECTED
        Log.d("ClassicBT", "🔌 Disconnected")
    }

    // ------------------------------------------------------------
    // SEND RESET COMMAND
    // ------------------------------------------------------------
    override fun sendReset() {
        scope.launch {
            try {
                socket?.outputStream?.write(
                    byteArrayOf(0xAA.toByte(), 0xAA.toByte(), 0x55, 0, 0, 0, 0)
                )
                Log.d("ClassicBT", "✅ Reset command sent")
            } catch (e: Exception) {
                Log.e("ClassicBT", "❌ Reset send failed: ${e.message}")
            }
        }
    }

    // ------------------------------------------------------------
    // START STREAMING SENSOR PACKETS
    // ------------------------------------------------------------
    override fun startStreaming() {
        scope.launch(Dispatchers.IO) {

            val out = socket?.outputStream ?: return@launch
            val input = socket?.inputStream ?: return@launch

            // ✅ Start command
            listOf(0xAA, 0xBB, 0x55, 0x00, 0x00, 0x00, 0x00).forEach { out.write(it) }

            val buffer = mutableListOf<Byte>()
            val tempBuffer = ByteArray(128)
            val dataStream = DataInputStream(BufferedInputStream(input, 1024))

            fun bytesToInt(high: Byte, low: Byte): Int {
                return ((high.toInt() shl 8) or (low.toInt() and 0xFF)).toShort().toInt()
            }

            fun convertAngle(angle: Int): Int {
                val normalized = (angle + 270) % 360
                return if (normalized > 180) normalized - 360 else normalized
            }

            fun convertCenterPitch(angle: Int): Int {
                val normalized = (angle + 90) % 360
                return if (normalized > 180) normalized - 360 else normalized
            }

            fun convertCenterRoll(angle: Int): Int {
                return if (angle > 180) angle - 360 else angle
            }

            Log.d("ClassicBT", "📡 Streaming started...")

            while (isActive && connectionState.value == ConnectionState.CONNECTED) {
                try {

                    if (dataStream.available() > 0) {

                        val bytesRead = dataStream.read(tempBuffer)

                        if (bytesRead > 0) {
                            buffer.addAll(tempBuffer.take(bytesRead))

                            // Extract packets
                            while (buffer.size >= 13) {
                                if (buffer[12] == 0xAA.toByte()) {

                                    val packet = buffer.subList(0, 13)

                                    val rRoll = convertAngle(bytesToInt(packet[0], packet[1])).toFloat()
                                    val rPitch = convertAngle(bytesToInt(packet[2], packet[3])).toFloat()
                                    val lRoll = convertAngle(bytesToInt(packet[4], packet[5])).toFloat()
                                    val lPitch = convertAngle(bytesToInt(packet[6], packet[7])).toFloat()
                                    val cRoll = convertCenterRoll(bytesToInt(packet[8], packet[9])).toFloat()
                                    val cPitch = convertCenterPitch(bytesToInt(packet[10], packet[11])).toFloat()

                                    _sensorData.value = SensorData(
                                        lPitch, lRoll,
                                        rPitch, rRoll,
                                        cPitch, cRoll
                                    )

                                    buffer.subList(0, 13).clear()

                                } else {
                                    buffer.removeAt(0)
                                }
                            }
                        }
                    } else {
                        delay(60)
                    }

                } catch (e: SocketTimeoutException) {
                    Log.e("ClassicBT", "Timeout: ${e.message}")
                } catch (e: Exception) {
                    Log.e("ClassicBT", "Streaming error: ${e.message}")
                    _connectionState.value = ConnectionState.DISCONNECTED
                    break
                }
            }
        }
    }

    // ------------------------------------------------------------
    // STOP STREAMING SENSOR PACKETS
    // ------------------------------------------------------------
    override fun stopStreaming() {
        scope.launch {
            try {
                val out = socket?.outputStream ?: return@launch
                delay(300)
                listOf(0xAA, 0xAB, 0x55, 0x00, 0x00, 0x00, 0x00).forEach { out.write(it) }
                out.flush()
                Log.d("ClassicBT", "⛔ Streaming stopped")
            } catch (e: Exception) {
                Log.e("ClassicBT", "Stop stream error: ${e.message}")
            }
        }
    }
}
