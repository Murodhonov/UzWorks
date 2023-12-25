package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.response.DistrictResponse
import dev.goblingroup.uzworks.networking.DistrictService
import kotlinx.coroutines.flow.Flow

class DistrictRepository(
    private val districtService: DistrictService,
    private val districtId: String,
    private val regionId: String
) {

    fun getDistrictById(): Flow<DistrictResponse> = districtService.getDistrictById(districtId = districtId)

    fun getAllDistricts() = districtService.getAllDistricts()

    fun getDistrictByRegionId() = districtService.getDistrictByRegionId(regionId = regionId)

}