package ripple.trackingmaster.devicetrackapp.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import ripple.trackingmaster.devicetrackapp.data.bluetooth.classic.ClassicBluetoothController
import ripple.trackingmaster.devicetrackapp.data.bluetooth.ble.BleBluetoothController
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    // Toggle true → Use BLE controller in future
    private const val USE_BLE = false

    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

    @Provides
    @Singleton
    fun provideBluetoothController(
        adapter: BluetoothAdapter
    ): BluetoothController {
        return if (USE_BLE) {
            BleBluetoothController()
        } else {
            ClassicBluetoothController(adapter)
        }
    }
}
