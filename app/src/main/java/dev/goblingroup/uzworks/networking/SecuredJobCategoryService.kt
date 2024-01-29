package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.JobCategoryEditRequest
import dev.goblingroup.uzworks.models.request.JobCategoryRequest
import dev.goblingroup.uzworks.models.response.JobCategoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SecuredJobCategoryService {

    @POST("api/JobCategory/Create")
    suspend fun createJobCategory(
        @Body jobCategoryRequest: JobCategoryRequest,
    ): Response<JobCategoryResponse>

    @DELETE("api/JobCategory/Delete/{id}")
    suspend fun deleteJobCategory(
        @Path("id") jobCategoryId: String
    ): Response<Response<Unit>>

    @PUT("api/JobCategory/Edit")
    suspend fun editJobCategory(
        @Body jobCategoryEditRequest: JobCategoryEditRequest,
    ): Response<Unit>

}