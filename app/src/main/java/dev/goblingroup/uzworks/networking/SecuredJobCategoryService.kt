package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.JobCategoryEditRequest
import dev.goblingroup.uzworks.models.request.JobCategoryRequest
import dev.goblingroup.uzworks.models.response.JobCategoryResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SecuredJobCategoryService {

    @POST("api/JobCategory/Create")
    fun createJobCategory(
        @Body jobCategoryRequest: JobCategoryRequest,
    ): Flow<JobCategoryResponse>

    @DELETE("api/JobCategory/Delete/{id}")
    fun deleteJobCategory(
        @Path("id") jobCategoryId: String
    ): Flow<Response<Unit>>

    @PUT("api/JobCategory/Edit")
    fun editJobCategory(
        @Body jobCategoryEditRequest: JobCategoryEditRequest,
    ): Flow<Response<Unit>>

}