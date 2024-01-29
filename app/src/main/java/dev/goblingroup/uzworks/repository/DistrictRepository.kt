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

    suspend fun addDistrict(districtEntity: DistrictEntity) = districtDao.addDistrict(districtEntity)

    suspend fun addDistricts(districtList: List<DistrictEntity>) = districtDao.addDistricts(districtList)

    suspend fun findDistrict(districtId: String) = districtDao.findDistrict(districtId)

    suspend fun listDistricts() = districtDao.listDistricts()

    suspend fun listDistrictsByRegionId(regionId: String) = districtDao.listDistrictsByRegionId(regionId)

    suspend fun getRegionByDistrictId(districtId: String) = districtDao.getRegionByDistrictId(districtId)

}