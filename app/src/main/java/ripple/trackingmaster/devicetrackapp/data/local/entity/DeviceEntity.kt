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
    val beltSize: String? = null,          // Optional size label (e.g. "34")
    val teamMember: String? = null,        // ✅ Name of person assigned
    val assignedSiteName: String? = null,  // ✅ Site name (e.g. "Apollo")
    val lastSeenStatus: String? = null,    // "Connected", "Disconnected"
    val createdAt: Long = System.currentTimeMillis()
)