package ripple.trackingmaster.devicetrackapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity

@Dao
interface SiteDao {

    @Query("SELECT * FROM sites ORDER BY createdAt DESC")
    fun observeSites(): Flow<List<SiteEntity>>

    @Query("SELECT * FROM sites WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): SiteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(site: SiteEntity)

    @Delete
    suspend fun delete(site: SiteEntity)
}
