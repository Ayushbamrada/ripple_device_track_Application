package ripple.trackingmaster.devicetrackapp.data.bluetooth.classic

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ripple.trackingmaster.devicetrackapp.ui.screens.ClassicScanViewModel

/**
 * BroadcastReceiver for HC-05 classic Bluetooth device discovery.
 */
class ClassicDiscoveryReceiver(
    private val onDeviceFound: (BluetoothDevice) -> Unit
) : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return

        when (intent.action) {

            BluetoothDevice.ACTION_FOUND -> {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                device?.let { onDeviceFound(it) }
            }
        }
    }
}
