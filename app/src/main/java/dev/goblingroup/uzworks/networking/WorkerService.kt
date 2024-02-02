package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.WorkerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WorkerService {

    @GET("api/Worker/GetById/{id}")
    suspend fun getById(
        @Path("id") workerId: String
    ): Response<WorkerResponse>

    @GET("api/Worker/GetAll")
    suspend fun getAll(
        @Path("pageNumber") pageNumber: Int? = null,
        @Path("pageSize") pageSize: Int = 10,
        @Path("jobCategoryId") jobCategoryId: String? = null,
        @Path("maxAge") maxAge: Int? = null,
        @Path("minAge") minAge: Int? = null,
        @Path("maxSalary") maxSalary: Int? = null,
        @Path("minSalary") minSalary: Int? = null,
        @Path("gender") gender: String? = null,
        @Path("regionId") regionId: String? = null,
        @Path("districtId") districtId: String? = null
    ): Response<List<WorkerResponse>>

    @GET("api/Worker/GetCount")
    suspend fun count(): Response<Int>

    @GET("api/Worker/GetWorkersByUserId/{id}")
    suspend fun getWorkersByUserId(
        @Path("id") userId: String
    ): Response<List<WorkerResponse>>

}