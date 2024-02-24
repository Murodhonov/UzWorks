package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.RegionDao
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.networking.RegionService
import javax.inject.Inject

class RegionRepository @Inject constructor(
    private val regionService: RegionService,
    private val regionDao: RegionDao
) {

    suspend fun getAllRegions() = regionService.getAll()

    fun addRegions(regionList: List<RegionEntity>) {
        regionDao.deleteRegions()
        regionDao.addRegions(regionList)
    }

    fun findRegionById(regionId: String) = regionDao.findRegion(regionId)

    fun listRegions() = regionDao.listRegions()

}