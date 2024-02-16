package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.DistrictDao
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.networking.DistrictService
import javax.inject.Inject

class DistrictRepository @Inject constructor(
    private val districtService: DistrictService,
    private val districtDao: DistrictDao
) {

    suspend fun getDistrictById(districtId: String) =
        districtService.getDistrictById(districtId = districtId)

    suspend fun getAllDistricts() = districtService.getAllDistricts()

    suspend fun getDistrictByRegionId(regionId: String) = districtService.getDistrictByRegionId(regionId = regionId)

    fun addDistricts(districtList: List<DistrictEntity>) = districtDao.addDistricts(districtList)

    fun findDistrict(districtId: String) = districtDao.findDistrict(districtId)

    fun listDistricts() = districtDao.listDistricts()

    fun listDistrictsByRegionId(regionId: String) = districtDao.listDistrictsByRegionId(regionId)

    fun getRegionByDistrictId(districtId: String) = districtDao.getRegionByDistrictId(districtId)

}