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
import ripple.trackingmaster.devicetrackapp.domain.repository.BluetoothController

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter {
        // We assume the device HAS Bluetooth. If manager.adapter is null, this will throw,
        // which is correct because the app cannot function without Bluetooth.
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter ?: throw IllegalStateException("Bluetooth not supported on this device")
    }

    @Provides
    @Singleton
    fun provideBluetoothController(
        adapter: BluetoothAdapter // Changed from BluetoothAdapter? to BluetoothAdapter
    ): BluetoothController {
        return ClassicBluetoothController(adapter)
    }
}