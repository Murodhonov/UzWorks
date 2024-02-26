package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.DistrictResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DistrictService {

    @GET("api/District/GetAll")
    suspend fun getAllDistricts(): Response<List<DistrictResponse>>

    @GET("api/District/GetByRegionId/{id}")
    suspend fun getDistrictsByRegionId(
        @Path("id") regionId: String
    ): Response<List<DistrictResponse>>

}