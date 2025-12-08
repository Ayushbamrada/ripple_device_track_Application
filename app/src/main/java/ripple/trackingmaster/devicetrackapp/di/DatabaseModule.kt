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
            .fallbackToDestructiveMigration() // This wipes data if schema changes (good for dev)
            .build()
    }

    @Provides
    @Singleton
    fun provideDeviceDao(db: AppDatabase): DeviceDao = db.deviceDao() // ✅ Uncommented

    @Provides
    @Singleton
    fun provideSiteDao(db: AppDatabase): SiteDao = db.siteDao()
}