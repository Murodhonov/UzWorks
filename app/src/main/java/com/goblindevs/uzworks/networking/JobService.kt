package com.goblindevs.uzworks.networking

import com.goblindevs.uzworks.models.request.JobCreateRequest
import com.goblindevs.uzworks.models.request.JobEditRequest
import com.goblindevs.uzworks.models.response.JobCreateResponse
import com.goblindevs.uzworks.models.response.JobResponse
import com.goblindevs.uzworks.utils.ConstValues.AUTH
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @POST("api/Job/Create")
    suspend fun createJob(
        @Header(AUTH) token: String,
        @Body jobCreateRequest: JobCreateRequest,
    ): Response<JobCreateResponse>

    @DELETE("api/Job/Delete/{id}")
    suspend fun deleteJob(
        @Header(AUTH) token: String,
        @Path("id") jobId: String
    ): Response<Unit>

    @PUT("api/Job/Update")
    suspend fun editJob(
        @Header(AUTH) token: String,
        @Body jobEditRequest: JobEditRequest
    ): Response<Unit>

    @GET("api/Job/GetByUserId/{id}")
    suspend fun getJobsByUserId(
        @Header(AUTH) token: String,
        @Path("id") userId: String
    ): Response<List<JobResponse>>

}