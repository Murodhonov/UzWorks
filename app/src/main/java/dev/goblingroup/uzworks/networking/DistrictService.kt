package dev.goblingroup.uzworks.networking

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DistrictService {

    @POST("api/District/Create")
    fun create(
        @Body regionId: String,
        @Body name: String
    ): Flow<Response<Unit>>

    @DELETE("api/District/Delete/{id}")
    fun delete(
        @Path("id") districtId: String
    ): Flow<Response<Unit>>

    @PUT("api/District/Edit")
    fun edit(
        @Body id: String,
        @Body regionId: String,
        @Body name: String,
    ): Flow<Response<Unit>>

    @GET("api/District/GetById/{id}")
    fun getById(
        @Path("id") districtId: String
    ): Flow<Response<Unit>>

    @GET("api/District/GetAll")
    fun getAll(): Flow<Response<Unit>>

    @GET("api/District/GetByRegionId/{id}")
    fun getByRegionId(
        @Path("id") regionId: String
    )

}