package ripple.trackingmaster.devicetrackapp.data.repo

import kotlinx.coroutines.flow.Flow
import ripple.trackingmaster.devicetrackapp.data.local.dao.DeviceDao
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
import javax.inject.Inject

class DeviceRepository @Inject constructor(
    private val dao: DeviceDao
) {

    fun observeDevices(): Flow<List<DeviceEntity>> =
        dao.observeDevices()

    suspend fun getDevice(mac: String): DeviceEntity? =
        dao.getDevice(mac)

    suspend fun saveDevice(entity: DeviceEntity) {
        dao.upsertDevice(entity)
    }

    suspend fun deleteDevice(entity: DeviceEntity) {
        dao.deleteDevice(entity)
    }
}
