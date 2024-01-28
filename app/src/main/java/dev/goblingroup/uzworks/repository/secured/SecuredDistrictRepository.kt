package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.DistrictEditRequest
import dev.goblingroup.uzworks.models.request.DistrictRequest
import dev.goblingroup.uzworks.networking.SecuredDistrictService
import javax.inject.Inject

class SecuredDistrictRepository @Inject constructor(
    private val securedDistrictService: SecuredDistrictService
) {

    suspend fun createDistrict(districtRequest: DistrictRequest) = securedDistrictService.createDistrict(districtRequest = districtRequest)

    suspend fun deleteDistrict(districtId: String) = securedDistrictService.deleteDistrict(districtId = districtId)

    suspend fun editDistrict(districtEditRequest: DistrictEditRequest) =
        securedDistrictService.editDistrict(districtEditRequest = districtEditRequest)

}