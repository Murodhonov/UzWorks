package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.DistrictEditRequest
import dev.goblingroup.uzworks.models.request.DistrictRequest
import dev.goblingroup.uzworks.networking.SecuredDistrictService

class SecuredDistrictRepository(
    private val securedDistrictService: SecuredDistrictService,
    private val districtRequest: DistrictRequest,
    var districtId: String,
    private val districtEditRequest: DistrictEditRequest
) {

    fun createDistrict() = securedDistrictService.createDistrict(districtRequest = districtRequest)

    fun deleteDistrict() = securedDistrictService.deleteDistrict(districtId = districtId)

    fun editDistrict() =
        securedDistrictService.editDistrict(districtEditRequest = districtEditRequest)

}