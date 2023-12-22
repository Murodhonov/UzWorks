package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.DistrictResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DistrictService {

    @GET("api/District/GetById/{id}")
    fun getDistrictById(
        @Path("id") districtId: String
    ): Flow<Response<Unit>>

    @GET("api/District/GetAll")
    fun getAllDistricts(): Flow<List<DistrictResponse>>

    @GET("api/District/GetByRegionId/{id}")
    fun getDistrictByRegionId(
        @Path("id") regionId: String
    ): Flow<Response<Unit>>

}