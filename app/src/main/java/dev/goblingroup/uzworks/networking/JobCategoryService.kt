package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.JobCategoryResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface JobCategoryService {

    @GET("api/JobCategory/GetById/{id}")
    fun getJobCategoryById(
        @Path("id") jobCategoryId: String
    ): Flow<Response<Unit>>

    @GET("api/JobCategory/GetAll")
    fun getAllJobCategories(): Flow<List<JobCategoryResponse>>

}