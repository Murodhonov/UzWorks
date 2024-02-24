package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.networking.RegionService
import javax.inject.Inject

class AddressRepository @Inject constructor(
    private val regionService: RegionService,
    private val districtService: DistrictService
) {

    suspend fun getRegionById(regionId: String) = regionService.getById(regionId)

    suspend fun getRegions() = regionService.getAllRegions()

    suspend fun getRegionByDistrictId(districtId: String) =
        regionService.getRegionByDistrictId(districtId)

    suspend fun getDistricts() = districtService.getAllDistricts()

    suspend fun getDistrictsByRegionId(regionId: String) =
        districtService.districtsByRegionId(regionId)

}