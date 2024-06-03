package dev.goblingroup.uzworks.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.goblingroup.uzworks.database.entity.RegionEntity

@Dao
interface RegionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRegions(regionList: List<RegionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRegion(region: RegionEntity)

    @Query("SELECT * FROM region_table WHERE region_id = :regionId")
    fun findRegion(regionId: String): RegionEntity

    @Query("SELECT * FROM region_table")
    fun listRegions(): List<RegionEntity>

    @Query("DELETE FROM region_table")
    fun deleteRegions()

    @Query("SELECT COUNT(*) FROM region_table")
    fun countRegions(): Int
}