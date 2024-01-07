package dev.goblingroup.uzworks.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.goblingroup.uzworks.database.entity.DistrictEntity

@Dao
interface DistrictDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDistrict(districtEntity: DistrictEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDistricts(districtList: List<DistrictEntity>)

    @Query("SELECT * FROM district_table WHERE district_id = :districtId")
    fun findDistrict(districtId: String)

    @Query("SELECT * FROM district_table")
    fun listDistricts(): List<DistrictEntity>

    @Query("SELECT * FROM district_table WHERE region_id = :regionId")
    fun listDistrictsByRegionId(regionId: String): List<DistrictEntity>

}