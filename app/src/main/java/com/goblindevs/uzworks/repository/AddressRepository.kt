package com.goblindevs.uzworks.repository

import com.goblindevs.uzworks.database.dao.DistrictDao
import com.goblindevs.uzworks.database.dao.RegionDao
import com.goblindevs.uzworks.database.entity.DistrictEntity
import com.goblindevs.uzworks.mapper.mapToEntity
import com.goblindevs.uzworks.models.response.DistrictResponse
import com.goblindevs.uzworks.models.response.RegionResponse
import com.goblindevs.uzworks.networking.DistrictService
import com.goblindevs.uzworks.networking.RegionService
import javax.inject.Inject

class AddressRepository @Inject constructor(
    private val regionService: RegionService,
    private val districtService: DistrictService,
    private val regionDao: RegionDao,
    private val districtDao: DistrictDao
) {

    suspend fun getDistrictById(districtId: String) = districtService.getDistrictById(districtId)

    suspend fun getRegionByDistrictId(districtId: String) = regionService.getByDistrictId(districtId)

    suspend fun getAllRegions() = regionService.getAllRegions()

    fun addRegions(regionList: List<RegionResponse>): Boolean {
        regionList.forEach { regionResponse ->
            regionDao.addRegion(regionResponse.mapToEntity())
        }
        return regionDao.countRegions() == regionList.size
    }

    fun findRegion(regionId: String) = regionDao.findRegion(regionId)

    fun listRegions() = regionDao.listRegions()

    suspend fun getAllDistricts() = districtService.getAllDistricts()

    suspend fun getDistrictsByRegionId(regionId: String) =
        districtService.getDistrictsByRegionId(regionId)

    fun addDistricts(districtList: List<DistrictResponse>, regionId: String): Boolean {
        districtList.forEach { districtResponse ->
            districtDao.addDistrict(districtResponse.mapToEntity(regionId))
        }
        return districtDao.listDistrictsByRegionId(regionId).size == districtList.size
    }

    fun findDistrict(districtId: String) = districtDao.findDistrict(districtId)

    fun listDistricts() = districtDao.listDistricts()

    fun listDistrictsByRegionId(regionId: String) = districtDao.listDistrictsByRegionId(regionId)

    fun findRegionByDistrictId(districtId: String) = districtDao.findRegionByDistrictId(districtId)

    fun districtsByRegionName(regionName: String): List<DistrictEntity> {
        return districtDao.listDistrictsByRegionId(
            regionDao.listRegions().find { it.name == regionName }!!.id)
    }

}