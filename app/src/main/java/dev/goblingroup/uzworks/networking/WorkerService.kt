package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.WorkerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WorkerService {

    @GET("api/Worker/GetById/{id}")
    suspend fun getById(
        @Path("id") userId: String
    ): Response<WorkerResponse>

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
    ): Response<List<WorkerResponse>>

    @GET("api/Worker/GetCount")
    suspend fun count(): Response<Int>

    @GET("api/Worker/GetWorkersByUserId/{id}")
    suspend fun getWorkersByUserId(
        @Path("id") userId: String
    ): Response<WorkerResponse>

}