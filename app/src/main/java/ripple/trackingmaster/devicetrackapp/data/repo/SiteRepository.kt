package ripple.trackingmaster.devicetrackapp.data.repo

import kotlinx.coroutines.flow.Flow
// import ripple.trackingmaster.devicetrackapp.data.local.dao.DeviceAssignmentDao <-- DELETE THIS
import ripple.trackingmaster.devicetrackapp.data.local.dao.SiteDao
// import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceAssignmentEntity <-- DELETE THIS
// import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity <-- DELETE THIS
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import javax.inject.Inject

class SiteRepository @Inject constructor(
    private val siteDao: SiteDao
    // private val assignDao: DeviceAssignmentDao <-- DELETE THIS
) {
    fun observeSites(): Flow<List<SiteEntity>> = siteDao.observeSites()

    suspend fun getSiteById(id: Int): SiteEntity? = siteDao.getById(id)

    suspend fun createSite(name: String, location: String?) {
        siteDao.insert(SiteEntity(siteName = name, location = location))
    }

    // --- DELETE ALL FUNCTIONS BELOW THIS LINE ---
    // fun observeDevicesForSite(siteId: Int): Flow<List<DeviceEntity>> = ...
    // suspend fun assignDeviceToSite(mac: String, siteId: Int) { ... }
    // suspend fun unassignDevice(mac: String) { ... }
    // fun observeSiteIdForDevice(mac: String): Flow<Int?> = ...
}