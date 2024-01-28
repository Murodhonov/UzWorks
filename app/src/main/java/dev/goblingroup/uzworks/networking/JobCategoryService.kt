package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.JobCategoryResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

interface JobCategoryService {

    @GET("api/JobCategory/GetById/{id}")
    suspend fun getJobCategoryById(
        @Path("id") jobCategoryId: String
    ): Flow<JobCategoryResponse>

    @GET("api/JobCategory/GetAll")
    suspend fun getAllJobCategories(): Flow<List<JobCategoryResponse>>

}