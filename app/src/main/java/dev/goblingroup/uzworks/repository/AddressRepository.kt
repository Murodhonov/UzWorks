package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.DistrictDao
import dev.goblingroup.uzworks.database.dao.RegionDao
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.networking.RegionService
import javax.inject.Inject

class AddressRepository @Inject constructor(
    private val regionService: RegionService,
    private val districtService: DistrictService,
    private val regionDao: RegionDao,
    private val districtDao: DistrictDao
) {

    suspend fun getAllRegions() = regionService.getAllRegions()

    fun addRegions(regionList: List<RegionEntity>): Boolean {
        regionDao.addRegions(regionList)
        return regionDao.countRegions() == regionList.size
    }

    fun findRegion(regionId: String) = regionDao.findRegion(regionId)

    fun listRegions() = regionDao.listRegions()

    suspend fun getAllDistricts() = districtService.getAllDistricts()

    suspend fun getDistrictsByRegionId(regionId: String) =
        districtService.getDistrictsByRegionId(regionId)

    fun addDistricts(districtList: List<DistrictEntity>): Boolean {
        districtDao.addDistricts(districtList)
        return districtDao.countDistricts() == districtList.size
    }

    fun findDistrict(districtId: String) = districtDao.findDistrict(districtId)

    fun listDistricts() = districtDao.listDistricts()

    fun listDistrictsByRegionId(regionId: String) = districtDao.listDistrictsByRegionId(regionId)

    fun findRegionByDistrictId(districtId: String) = districtDao.findRegionByDistrictId(districtId)

}