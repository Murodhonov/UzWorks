package com.goblindevs.uzworks.networking

import com.goblindevs.uzworks.models.response.JobCategoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface JobCategoryService {

    @GET("api/JobCategory/GetById/{id}")
    suspend fun getJobCategoryById(
        @Path("id") jobCategoryId: String
    ): Response<JobCategoryResponse>

    @GET("api/JobCategory/GetAll")
    suspend fun getAllJobCategories(): Response<List<JobCategoryResponse>>

}