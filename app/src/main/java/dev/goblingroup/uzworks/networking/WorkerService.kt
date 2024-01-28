package dev.goblingroup.uzworks.networking

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WorkerService {

    @GET("api/Worker/GetById/{id}")
    suspend fun getById(
        @Path("id") userId: String
    ): Flow<Response<Unit>>

    @GET("api/Worker/GetAll")
    suspend fun getAll(
        @Path("pageNumber") pageNumber: Int,
        @Path("pageSize") pageSize: Int,
        @Path("jobCategoryId") jobCategoryId: String,
        @Path("maxAge") maxAge: Int,
        @Path("minAge") minAge: Int,
        @Path("maxSalary") maxSalary: Int,
        @Path("minSalary") minSalary: Int,
        @Path("gender") gender: String,
        @Path("regionId") regionId: String,
        @Path("districtId") districtId: String,
    ): Flow<Response<Unit>>

    @GET("api/Worker/GetCount")
    suspend fun count(): Flow<Response<Unit>>

    @GET("api/Worker/GetWorkersByUserId/{id}")
    suspend fun getWorkersByUserId(
        @Path("id") userId: String
    ): Flow<Response<Unit>>

}