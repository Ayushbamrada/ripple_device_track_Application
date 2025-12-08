package ripple.trackingmaster.devicetrackapp.data.bluetooth.classic

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject

class ClassicBluetoothController @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter
) : BluetoothController {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableStateFlow("")
    override val incomingMessages: StateFlow<String> = _incomingMessages.asStateFlow()

    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    // Standard SPP UUID
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    override fun connect(macAddress: String) {
        if (_connectionState.value == ConnectionState.CONNECTED) return
        _connectionState.value = ConnectionState.CONNECTING

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress)
                bluetoothAdapter.cancelDiscovery()

                // ✅ USE INSECURE SOCKET (Matches Flutter behavior)
                socket = device.createInsecureRfcommSocketToServiceRecord(uuid)
                socket?.connect()

                inputStream = socket?.inputStream
                outputStream = socket?.outputStream

                _connectionState.value = ConnectionState.CONNECTED
                Log.d("ClassicBT", "✅ Connected (Insecure) to $macAddress")

                // Small delay to let connection settle
                delay(500)

                listenForData()

            } catch (e: IOException) {
                Log.e("ClassicBT", "Connection failed", e)
                closeConnection()
            }
        }
    }

    private fun listenForData() {
        val buffer = ByteArray(1024)
        Log.i("ClassicBT", "🎧 Reading Loop Started")

        while (_connectionState.value == ConnectionState.CONNECTED) {
            try {
                if (inputStream == null) break

                // ✅ BLOCKING READ: Waits here until data arrives
                val bytes = inputStream!!.read(buffer)

                if (bytes > 0) {
                    val rawData = buffer.copyOf(bytes)
                    // Convert to Hex String for consistency
                    val hexString = rawData.joinToString("") { "%02x".format(it) }

                    Log.i("ClassicBT", "🔥 DATA RECEIVED: $hexString")
                    _incomingMessages.value = hexString
                } else if (bytes == -1) {
                    Log.w("ClassicBT", "Stream closed")
                    disconnect()
                    break
                }
            } catch (e: IOException) {
                Log.e("ClassicBT", "Read error", e)
                disconnect()
                break
            }
        }
    }

    override fun disconnect() {
        closeConnection()
    }

    override fun sendCommand(command: String) {
        if (_connectionState.value != ConnectionState.CONNECTED || outputStream == null) {
            Log.e("ClassicBT", "Cannot send: Not connected")
            return
        }
        try {
            // ✅ DEBUG LOG: Show exactly what we are trying to send
            Log.d("ClassicBT", "Preparing to send HEX: $command")

            val bytes = hexStringToByteArray(command)

            // ✅ DEBUG LOG: Show the actual byte values (signed)
            Log.d("ClassicBT", "Sending Bytes: ${bytes.joinToString(",")}")

            outputStream?.write(bytes)
            outputStream?.flush()
            Log.d("ClassicBT", "✅ Data flushed to stream")

        } catch (e: Exception) {
            Log.e("ClassicBT", "Failed to send command", e)
            disconnect()
        }
    }

    // ✅ FIXED: Bulletproof Hex -> Byte converter
    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        try {
            for (i in 0 until len step 2) {
                data[i / 2] = ((Character.digit(s[i], 16) shl 4) +
                        Character.digit(s[i + 1], 16)).toByte()
            }
        } catch (e: Exception) {
            Log.e("ClassicBT", "Hex conversion failed. Check string format.", e)
        }
        return data
    }

    private fun closeConnection() {
        try {
            socket?.close()
            socket = null
            _connectionState.value = ConnectionState.DISCONNECTED
        } catch (e: Exception) { e.printStackTrace() }
    }
}