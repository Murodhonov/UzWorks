package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.DistrictEditRequest
import dev.goblingroup.uzworks.models.request.DistrictRequest
import dev.goblingroup.uzworks.models.response.DistrictResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SecuredDistrictService {

    @POST("api/District/Create")
    suspend fun createDistrict(
        @Body districtRequest: DistrictRequest,
    ): Response<DistrictResponse>

    @DELETE("api/District/Delete/{id}")
    suspend fun deleteDistrict(
        @Path("id") districtId: String
    ): Response<Unit>

    @PUT("api/District/Edit")
    suspend fun editDistrict(
        @Body districtEditRequest: DistrictEditRequest,
    ): Response<Unit>

}