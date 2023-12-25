package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.JobResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

interface JobService {

    @GET("api/Job/GetById/{id}")
    fun getJobById(
        @Path("id") jobId: String
    ): Flow<JobResponse>

    @GET("api/Job/GetAll")
    fun getAllJobs(
        @Path("pageNumber") pageNumber: Int = 1,
        @Path("pageSize") pageSize: Int = 10,
        @Path("jobCategoryId") jobCategoryId: String? = null,
        @Path("maxAge") maxAge: Int? = null,
        @Path("minAge") minAge: Int? = null,
        @Path("maxSalary") maxSalary: Int? = null,
        @Path("minSalary") minSalary: Int? = null,
        @Path("gender") gender: String? = null,
        @Path("regionId") regionId: String? = null,
        @Path("districtId") districtId: String? = null,
    ): Flow<List<JobResponse>>

    @GET("api/Job/GetCount")
    fun countJobs(): Flow<Int>

    @GET("api/Job/GetJobsByUserId/{id}")
    fun getJobsByUserId(
        @Path("id") userId: String
    ): Flow<List<JobResponse>>

}