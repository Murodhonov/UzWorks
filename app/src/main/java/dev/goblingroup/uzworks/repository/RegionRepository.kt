package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.networking.RegionService
import javax.inject.Inject

class RegionRepository @Inject constructor(
    private val regionService: RegionService
) {

    suspend fun getAllRegions() = regionService.getAllRegions()

}