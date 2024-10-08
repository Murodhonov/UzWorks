package com.goblindevs.uzworks.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goblindevs.uzworks.database.entity.DistrictEntity
import com.goblindevs.uzworks.database.entity.RegionEntity

@Dao
interface DistrictDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDistricts(districtList: List<DistrictEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDistrict(district: DistrictEntity)

    @Query("SELECT * FROM district_table WHERE district_id = :districtId")
    fun findDistrict(districtId: String): DistrictEntity

    @Query("SELECT * FROM district_table")
    fun listDistricts(): List<DistrictEntity>

    @Query("SELECT * FROM district_table WHERE region_id = :regionId")
    fun listDistrictsByRegionId(regionId: String): List<DistrictEntity>

    @Query("SELECT * FROM region_table INNER JOIN district_table ON region_table.region_id = district_table.region_id WHERE district_table.district_id = :districtId")
    fun findRegionByDistrictId(districtId: String): RegionEntity

    @Query("DELETE FROM district_table")
    fun deleteDistricts()

    @Query("SELECT COUNT(*) FROM district_table")
    fun countDistricts(): Int

    @Query("DELETE FROM district_table")
    fun clearTable()

}