package dev.goblingroup.uzworks.networking

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface JobService {

    @GET("api/Job/GetById/{id}")
    fun getJobById(
        @Path("id") id: String
    ): Flow<Response<Unit>>

    @GET("api/Job/GetAll")
    fun getAllJobs(
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

    @GET("api/Job/GetCount")
    fun countJobs(): Flow<Response<Unit>>

    @GET("api/Job/GetJobsByUserId/{id}")
    fun getJobsByUserId(
        @Path("id") userId: String
    ): Flow<Response<Unit>>

}