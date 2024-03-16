package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.JobResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JobService {

    @GET("api/Job/GetById/{id}")
    suspend fun getJobById(
        @Path("id") jobId: String
    ): Response<JobResponse>

    @GET("api/Job/GetAll")
    suspend fun getAllJobs(
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
    ): Response<List<JobResponse>>

    @GET("api/Job/GetTopJobs")
    suspend fun getTopJobs(): Response<List<JobResponse>>

    @GET("api/Job/GetCount/{status}")
    suspend fun countJobs(
        @Path("status") status: Boolean = false
    ): Response<Int>

}