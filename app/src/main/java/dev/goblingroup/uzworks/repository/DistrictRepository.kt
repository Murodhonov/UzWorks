package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.networking.DistrictService
import javax.inject.Inject

class DistrictRepository @Inject constructor(
    private val districtService: DistrictService,
    private val districtDao: DistrictDao
) {

    suspend fun getAllDistricts() = districtService.getAllDistricts()

    fun addDistricts(districtList: List<DistrictEntity>) {
        districtDao.deleteDistricts()
        districtDao.addDistricts(districtList)
    }

    fun findDistrict(districtId: String) = districtDao.findDistrict(districtId)

    fun listDistricts() = districtDao.listDistricts()

    fun listDistrictsByRegionId(regionId: String) = districtDao.listDistrictsByRegionId(regionId)

    fun getRegionByDistrictId(districtId: String) = districtDao.getRegionByDistrictId(districtId)

}