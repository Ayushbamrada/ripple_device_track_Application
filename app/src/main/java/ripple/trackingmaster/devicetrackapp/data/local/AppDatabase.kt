package ripple.trackingmaster.devicetrackapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ripple.trackingmaster.devicetrackapp.data.local.dao.DeviceDao
import ripple.trackingmaster.devicetrackapp.data.local.dao.SiteDao
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity

@Database(
    entities = [
        DeviceEntity::class,  // ✅ Uncommented (Added back)
        SiteEntity::class
    ],
    version = 4, // ✅ Bumped version to 4
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao // ✅ Uncommented (Added back)
    abstract fun siteDao(): SiteDao
}