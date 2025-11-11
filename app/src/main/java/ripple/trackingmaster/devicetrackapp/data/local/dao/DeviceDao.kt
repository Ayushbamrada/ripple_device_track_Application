package ripple.trackingmaster.devicetrackapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices ORDER BY lastSeen DESC")
    fun observeDevices(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE mac = :mac LIMIT 1")
    suspend fun getDevice(mac: String): DeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDevice(entity: DeviceEntity)

    @Delete
    suspend fun deleteDevice(entity: DeviceEntity)
}
