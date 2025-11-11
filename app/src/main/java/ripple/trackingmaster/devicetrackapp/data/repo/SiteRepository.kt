package ripple.trackingmaster.devicetrackapp.data.repo

import kotlinx.coroutines.flow.Flow
import ripple.trackingmaster.devicetrackapp.data.local.dao.DeviceAssignmentDao
import ripple.trackingmaster.devicetrackapp.data.local.dao.SiteDao
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceAssignmentEntity
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import javax.inject.Inject

class SiteRepository @Inject constructor(
    private val siteDao: SiteDao,
    private val assignDao: DeviceAssignmentDao
) {
    fun observeSites(): Flow<List<SiteEntity>> = siteDao.observeSites()

    suspend fun getSiteById(id: Int): SiteEntity? = siteDao.getById(id)

    suspend fun createSite(name: String, location: String?) {
        siteDao.insert(SiteEntity(siteName = name, location = location))
    }

    fun observeDevicesForSite(siteId: Int): Flow<List<DeviceEntity>> =
        assignDao.observeDevicesForSite(siteId)

    suspend fun assignDeviceToSite(mac: String, siteId: Int) {
        assignDao.assign(DeviceAssignmentEntity(mac = mac, siteId = siteId))
    }

    suspend fun unassignDevice(mac: String) {
        assignDao.unassignDevice(mac)
    }
}
