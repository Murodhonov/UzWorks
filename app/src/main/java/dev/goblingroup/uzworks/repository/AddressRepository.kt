package dev.goblingroup.uzworks.repository

import android.util.Log
import dev.goblingroup.uzworks.database.dao.DistrictDao
import dev.goblingroup.uzworks.database.dao.RegionDao
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.networking.RegionService
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import javax.inject.Inject

class AddressRepository @Inject constructor(
    private val regionService: RegionService,
    private val districtService: DistrictService,
    private val regionDao: RegionDao,
    private val districtDao: DistrictDao
) {

    suspend fun getAllRegions() = regionService.getAll()

    fun addRegions(regionList: List<RegionEntity>): Boolean {
        regionDao.deleteRegions()
        regionDao.addRegions(regionList)
        Log.d(TAG, "addRegions: ${regionList.size} regions added $regionList")
        return regionDao.countRegions() == regionList.size
    }

    fun findRegion(regionId: String) = regionDao.findRegion(regionId)

    fun listRegions() = regionDao.listRegions()

    suspend fun getAllDistricts() = districtService.getAllDistricts()

    suspend fun getDistrictsByRegionId(regionId: String) =
        districtService.getDistrictsByRegionId(regionId)

    fun addDistrict(districtEntity: DistrictEntity) = districtDao.addDistrict(districtEntity)

    fun findDistrict(districtId: String) = districtDao.findDistrict(districtId)

    fun listDistricts() = districtDao.listDistricts()

    fun listDistrictsByRegionId(regionId: String) = districtDao.listDistrictsByRegionId(regionId)

    fun findRegionByDistrictId(districtId: String) = districtDao.findRegionByDistrictId(districtId)

}