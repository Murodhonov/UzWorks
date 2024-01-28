package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.models.request.JobRequest
import dev.goblingroup.uzworks.models.response.JobResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SecuredJobService {

    @POST("api/Job/Create")
    suspend fun createJob(
        @Body jobRequest: JobRequest,
    ): Flow<JobResponse>

    @DELETE("api/Job/Delete/{id}")
    suspend fun deleteJob(
        @Path("id") jobId: String
    ): Flow<Response<Unit>>

    @PUT("api/Job/Edit")
    suspend fun editJob(
        @Body jobEditRequest: JobEditRequest
    ): Flow<Response<Unit>>

}