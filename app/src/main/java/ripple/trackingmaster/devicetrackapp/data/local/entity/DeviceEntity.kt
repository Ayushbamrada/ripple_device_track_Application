package ripple.trackingmaster.devicetrackapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents one physical HipPro belt device.
 *
 * MAC is primary key because:
 *  🔹 HC-05 always exposes fixed MAC
 *  🔹 Easy to reference
 *  🔹 Backend can map Serial ↔ MAC later
 */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val mac: String,

    val name: String? = null,
    val serial: String? = null,

    // Last time we connected or scanned the device
    val lastSeen: Long = System.currentTimeMillis(),

    // Optional future: site assignment or status
    val site: String? = null,
    val status: String? = "unassigned"
)
