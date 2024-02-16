package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.WorkerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkerService {

    @GET("api/Worker/GetById/{id}")
    suspend fun getById(
        @Path("id") workerId: String
    ): Response<WorkerResponse>

    @GET("api/Worker/GetAll")
    suspend fun getAll(
        @Query("pageNumber") pageNumber: Int? = null,
        @Query("pageSize") pageSize: Int = 10,
        @Query("jobCategoryId") jobCategoryId: String? = null,
        @Query("maxAge") maxAge: Int? = null,
        @Query("minAge") minAge: Int? = null,
        @Query("maxSalary") maxSalary: Int? = null,
        @Query("minSalary") minSalary: Int? = null,
        @Query("gender") gender: String? = null,
        @Query("regionId") regionId: String? = null,
        @Query("districtId") districtId: String? = null
    ): Response<List<WorkerResponse>>

    @GET("api/Worker/GetCount")
    suspend fun count(): Response<Int>

    @GET("api/Worker/GetWorkersByUserId/{id}")
    suspend fun getWorkersByUserId(
        @Path("id") userId: String
    ): Response<List<WorkerResponse>>

}