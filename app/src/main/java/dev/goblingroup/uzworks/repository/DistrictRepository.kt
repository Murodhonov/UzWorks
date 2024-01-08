package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.DistrictDao
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.models.response.DistrictResponse
import dev.goblingroup.uzworks.networking.DistrictService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DistrictRepository(
    private val districtService: DistrictService,
    private val districtDao: DistrictDao,
    private val districtId: String,
    private val regionId: String
) {

    fun getDistrictById(): Flow<DistrictResponse> =
        districtService.getDistrictById(districtId = districtId)

    fun getAllDistricts() = districtService.getAllDistricts()

    fun getDistrictByRegionId() = districtService.getDistrictByRegionId(regionId = regionId)

    fun addDistrict(districtEntity: DistrictEntity) = districtDao.addDistrict(districtEntity)

    fun addDistricts(districtList: List<DistrictEntity>) =
        flow { emit(districtDao.addDistricts(districtList)) }

    fun findDistrict(districtId: String) = districtDao.findDistrict(districtId)

    fun listDistricts() = districtDao.listDistricts()

    fun listDistrictsByRegionId(regionId: String) = districtDao.listDistrictsByRegionId(regionId)

    fun getRegionByDistrictId(districtId: String) = districtDao.getRegionByDistrictId(districtId)

}