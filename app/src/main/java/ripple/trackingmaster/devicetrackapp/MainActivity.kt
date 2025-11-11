package ripple.trackingmaster.devicetrackapp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ripple.trackingmaster.devicetrackapp.ui.navigation.AppNavigation
import ripple.trackingmaster.devicetrackapp.ui.screens.ClassicScanViewModel
//import ripple.trackingmaster.devicetrackapp.ui.screens.ClassicScanViewModel
import ripple.trackingmaster.devicetrackapp.ui.theme.HipProTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val scanVm: ClassicScanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerBluetoothReceiver()

        setContent {
            HipProTheme {
                AppNavigation()
            }
        }
    }

    private fun registerBluetoothReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }

        registerReceiver(
            object : android.content.BroadcastReceiver() {
                @SuppressLint("MissingPermission")
                override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
                    when (intent?.action) {

                        BluetoothDevice.ACTION_FOUND -> {
                            val device: BluetoothDevice? =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                            if (device != null) {
                                scanVm.onDeviceFound(
                                    device.name ?: "Unknown",
                                    device.address
                                )
                            }
                        }

                        BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                            scanVm.onScanFinished()
                        }
                    }
                }
            },
            filter
        )
    }
}
