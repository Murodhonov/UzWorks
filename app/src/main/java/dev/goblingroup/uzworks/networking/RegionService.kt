package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.RegionResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

interface RegionService {

    @GET("api/Region/GetById/{id}")
    fun getById(
        @Path("id") regionId: String
    ): Flow<RegionResponse>

    @GET("api/Region/GetAll")
    fun getAll(): Flow<List<RegionResponse>>

    @GET("api/Region/GetByRegionByDistrictId/{id}")
    fun getRegionByDistrictId(
        @Path("id") districtId: String
    ): Flow<RegionResponse>

}