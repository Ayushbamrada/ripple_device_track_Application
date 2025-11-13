package ripple.trackingmaster.devicetrackapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents one physical HipPro belt device.
 * Stored locally in Room DB.
 */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val mac: String,           // Unique MAC address
    val serialNumber: String? = null,      // Belt’s serial number
    val customName: String? = null,        // Editable display name (e.g., "Hip-Pro 1")
    val beltNumber: String? = null,           // Optional user-assigned number
    val beltSize: String? = null,          // Optional size label
    val lastSeenStatus: String? = null,    // "Connected", "Disconnected", etc.
    val createdAt: Long = System.currentTimeMillis()
)
