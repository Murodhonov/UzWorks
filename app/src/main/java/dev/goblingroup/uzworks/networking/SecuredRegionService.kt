package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.RegionEditRequest
import dev.goblingroup.uzworks.models.response.RegionResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SecuredRegionService {

    @POST("api/Region/Create")
    fun createRegion(
        @Body regionName: String
    ): Flow<RegionResponse>

    @DELETE("api/Region/Delete/{id}")
    fun deleteRegion(
        @Path("id") regionId: String
    ): Flow<Response<Unit>>

    @PUT("api/Region/Edit")
    fun editRegion(
        @Body regionEditRequest: RegionEditRequest,
    ): Flow<Response<Unit>>

}