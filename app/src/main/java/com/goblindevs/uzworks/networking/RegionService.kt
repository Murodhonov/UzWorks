package com.goblindevs.uzworks.networking

import com.goblindevs.uzworks.models.response.RegionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RegionService {

    @GET("api/Region/GetById/{id}")
    suspend fun getById(
        @Path("id") regionId: String
    ): Response<RegionResponse>

    @GET("api/Region/GetAll")
    suspend fun getAllRegions(): Response<List<RegionResponse>>

    @GET("api/Region/GetByDistrictId/{id}")
    suspend fun getByDistrictId(
        @Path("id") districtId: String
    ): Response<RegionResponse>

}