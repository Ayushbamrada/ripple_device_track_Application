package ripple.trackingmaster.devicetrackapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_assignments")
data class DeviceAssignmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val mac: String,       // belt MAC
    val siteId: Int,       // site ID
    val assignedAt: Long = System.currentTimeMillis()
)
