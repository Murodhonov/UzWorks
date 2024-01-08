package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.RegionDao
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.networking.RegionService
import kotlinx.coroutines.flow.flow

class RegionRepository(
    private val regionService: RegionService,
    private val regionDao: RegionDao,
    private val regionId: String,
    private val districtId: String
) {

    fun getRegionById() = regionService.getById(regionId)

    fun getAllRegions() = regionService.getAll()

    fun getRegionByDistrictId() = regionService.getRegionByDistrictId(districtId)

    fun addRegion(regionEntity: RegionEntity) = regionDao.addRegion(regionEntity)

    fun addRegions(regionList: List<RegionEntity>) = flow { emit(regionDao.addRegions(regionList)) }

    fun findRegionById(regionId: String) = regionDao.findRegion(regionId)

    fun listRegions() = regionDao.listRegions()

}