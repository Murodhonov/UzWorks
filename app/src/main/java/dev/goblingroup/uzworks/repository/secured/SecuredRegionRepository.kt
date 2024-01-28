package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.RegionEditRequest
import dev.goblingroup.uzworks.networking.SecuredRegionService
import javax.inject.Inject

class SecuredRegionRepository @Inject constructor(
    private val securedRegionService: SecuredRegionService
) {

    suspend fun createRegion(regionName: String) = securedRegionService.createRegion(regionName = regionName)

    suspend fun deleteRegion(regionId: String) = securedRegionService.deleteRegion(regionId = regionId)

    suspend fun editRegion(regionEditRequest: RegionEditRequest) =
        securedRegionService.editRegion(regionEditRequest = regionEditRequest)

}