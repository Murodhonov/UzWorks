package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.RegionEditRequest
import dev.goblingroup.uzworks.models.response.RegionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SecuredRegionService {

    @POST("api/Region/Create")
    suspend fun createRegion(
        @Body regionName: String
    ): Response<RegionResponse>

    @DELETE("api/Region/Delete/{id}")
    suspend fun deleteRegion(
        @Path("id") regionId: String
    ): Response<Unit>

    @PUT("api/Region/Edit")
    suspend fun editRegion(
        @Body regionEditRequest: RegionEditRequest,
    ): Response<Unit>

}