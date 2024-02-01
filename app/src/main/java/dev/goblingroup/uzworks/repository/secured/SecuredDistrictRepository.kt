package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.DistrictCreateRequest
import dev.goblingroup.uzworks.models.request.DistrictEditRequest
import dev.goblingroup.uzworks.networking.SecuredDistrictService
import javax.inject.Inject

class SecuredDistrictRepository @Inject constructor(
    private val securedDistrictService: SecuredDistrictService
) {

    suspend fun createDistrict(districtCreateRequest: DistrictCreateRequest) = securedDistrictService.createDistrict(districtCreateRequest = districtCreateRequest)

    suspend fun deleteDistrict(districtId: String) = securedDistrictService.deleteDistrict(districtId = districtId)

    suspend fun editDistrict(districtEditRequest: DistrictEditRequest) =
        securedDistrictService.editDistrict(districtEditRequest = districtEditRequest)

}