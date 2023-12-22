package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.RegionEditRequest
import dev.goblingroup.uzworks.networking.SecuredRegionService

class SecuredRegionRepository(
    private val securedRegionService: SecuredRegionService,
    private val regionName: String,
    private val regionId: String,
    private val regionEditRequest: RegionEditRequest
) {

    fun createRegion() = securedRegionService.createRegion(regionName = regionName)

    fun deleteRegion() = securedRegionService.deleteRegion(regionId = regionId)

    fun editRegion() =
        securedRegionService.editRegion(regionEditRequest = regionEditRequest)

}