package ripple.trackingmaster.devicetrackapp.data.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ripple.trackingmaster.devicetrackapp.data.local.dao.DeviceDao
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
import ripple.trackingmaster.devicetrackapp.data.remote.BeltApiService
import ripple.trackingmaster.devicetrackapp.data.remote.dto.BeltApiResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkBeltRepository @Inject constructor(
    private val api: BeltApiService,
    private val deviceDao: DeviceDao // ✅ Injecting the DAO
) {

    // ✅ Source of Truth: The Database
    val belts: Flow<List<BeltApiResponse>> = deviceDao.observeDevices().map { entities ->
        entities.map { entity ->
            BeltApiResponse(
                macAddress = entity.mac,
                serialNumber = entity.serialNumber,
                beltSize = entity.beltSize,
                teamMember = entity.teamMember,
                assignedTo = entity.assignedSiteName
            )
        }
    }
    // ✅ NEW: Get a single device by MAC (used for pre-filling)
    suspend fun getBeltByMac(mac: String): BeltApiResponse? {
        val entity = deviceDao.getDevice(mac) ?: return null
        return BeltApiResponse(
            macAddress = entity.mac,
            serialNumber = entity.serialNumber,
            beltSize = entity.beltSize,
            teamMember = entity.teamMember,
            assignedTo = entity.assignedSiteName
        )
    }

    fun refreshBelts() {
        // Optional: Sync from server
    }

    suspend fun syncBelt(belt: BeltApiResponse) {
        // 1. (Optional) Sync to Server
        // api.saveBelt(belt)

        // 2. ✅ Save to Local Database (Persistence)
        val entity = DeviceEntity(
            mac = belt.macAddress,
            serialNumber = belt.serialNumber,
            beltSize = belt.beltSize,
            teamMember = belt.teamMember,
            assignedSiteName = belt.assignedTo
        )
        deviceDao.upsertDevice(entity)
    }
}