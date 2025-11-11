package ripple.trackingmaster.devicetrackapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceAssignmentEntity
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity

@Dao
interface DeviceAssignmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun assign(assign: DeviceAssignmentEntity)

    @Query("""
        SELECT devices.* FROM devices 
        INNER JOIN device_assignments 
        ON devices.mac = device_assignments.mac
        WHERE device_assignments.siteId = :siteId
    """)
    fun observeDevicesForSite(siteId: Int): Flow<List<DeviceEntity>>

    @Query("DELETE FROM device_assignments WHERE mac = :mac")
    suspend fun unassignDevice(mac: String)
}
