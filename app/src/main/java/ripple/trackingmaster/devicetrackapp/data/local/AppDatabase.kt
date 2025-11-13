package ripple.trackingmaster.devicetrackapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ripple.trackingmaster.devicetrackapp.data.local.dao.DeviceDao
import ripple.trackingmaster.devicetrackapp.data.local.dao.SiteDao
import ripple.trackingmaster.devicetrackapp.data.local.dao.DeviceAssignmentDao
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceAssignmentEntity

@Database(
    entities = [
        DeviceEntity::class,
        SiteEntity::class,
        DeviceAssignmentEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao
    abstract fun siteDao(): SiteDao
    abstract fun deviceAssignmentDao(): DeviceAssignmentDao
}
