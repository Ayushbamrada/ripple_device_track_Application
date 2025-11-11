package ripple.trackingmaster.devicetrackapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ripple.trackingmaster.devicetrackapp.data.local.AppDatabase
import ripple.trackingmaster.devicetrackapp.data.local.dao.DeviceDao
import ripple.trackingmaster.devicetrackapp.data.local.dao.SiteDao
import ripple.trackingmaster.devicetrackapp.data.local.dao.DeviceAssignmentDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "hippro_devices.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDeviceDao(db: AppDatabase): DeviceDao = db.deviceDao()

    @Provides
    @Singleton
    fun provideSiteDao(db: AppDatabase): SiteDao = db.siteDao()

    @Provides
    @Singleton
    fun provideDeviceAssignmentDao(db: AppDatabase): DeviceAssignmentDao =
        db.deviceAssignmentDao()
}
