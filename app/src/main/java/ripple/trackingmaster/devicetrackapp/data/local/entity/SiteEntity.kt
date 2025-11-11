package ripple.trackingmaster.devicetrackapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a hospital / trial location.
 * Example: AIIMS Delhi, Apollo Hyderabad, Fortis Bangalore.
 */
@Entity(tableName = "sites")
data class SiteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val siteName: String,
    val location: String?,
    val createdAt: Long = System.currentTimeMillis()
)
