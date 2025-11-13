package ripple.trackingmaster.devicetrackapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity

@Dao
interface DeviceDao {

    // ✅ Observe all devices (sorted by creation time — newest first)
    @Query("SELECT * FROM devices ORDER BY createdAt DESC")
    fun observeDevices(): Flow<List<DeviceEntity>>

    // ✅ Get single device by MAC
    @Query("SELECT * FROM devices WHERE mac = :mac LIMIT 1")
    suspend fun getDevice(mac: String): DeviceEntity?

    // ✅ Insert or update (replaces if same MAC exists)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDevice(entity: DeviceEntity)

    // ✅ Update only specific fields if needed (optional helper)
    @Update
    suspend fun updateDevice(entity: DeviceEntity)

    // ✅ Delete a specific device
    @Delete
    suspend fun deleteDevice(entity: DeviceEntity)

    // ✅ Clear all stored devices
    @Query("DELETE FROM devices")
    suspend fun clearAll()
}
