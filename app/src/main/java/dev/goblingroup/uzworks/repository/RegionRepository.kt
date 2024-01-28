package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.RegionDao
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.networking.RegionService
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegionRepository @Inject constructor(
    private val regionService: RegionService,
    private val regionDao: RegionDao
) {

    suspend fun getRegionById(regionId: String) = regionService.getById(regionId)

    suspend fun getAllRegions() = regionService.getAll()

    suspend fun getRegionByDistrictId(districtId: String) = regionService.getRegionByDistrictId(districtId)

    fun addRegion(regionEntity: RegionEntity) = regionDao.addRegion(regionEntity)

    fun addRegions(regionList: List<RegionEntity>) = flow { emit(regionDao.addRegions(regionList)) }

    fun findRegionById(regionId: String) = regionDao.findRegion(regionId)

    fun listRegions() = regionDao.listRegions()

}