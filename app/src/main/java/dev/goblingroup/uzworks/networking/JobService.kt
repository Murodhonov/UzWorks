package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.JobResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JobService {

    @GET("api/Job/GetById/{id}")
    fun getJobById(
        @Path("id") jobId: String
    ): Flow<JobResponse>

    @GET("api/Job/GetAll")
    fun getAllJobs(
        @Query("pageNumber") pageNumber: Int? = null,
        @Query("pageSize") pageSize: Int = 10,
        @Query("jobCategoryId") jobCategoryId: String? = null,
        @Query("maxAge") maxAge: Int? = null,
        @Query("minAge") minAge: Int? = null,
        @Query("maxSalary") maxSalary: Int? = null,
        @Query("minSalary") minSalary: Int? = null,
        @Query("gender") gender: String? = null,
        @Query("regionId") regionId: String? = null,
        @Query("districtId") districtId: String? = null,
    ): Flow<List<JobResponse>>

    @GET("api/Job/GetCount")
    fun countJobs(): Flow<Int>

    @GET("api/Job/GetJobsByUserId/{id}")
    fun getJobsByUserId(
        @Path("id") userId: String
    ): Flow<List<JobResponse>>

}