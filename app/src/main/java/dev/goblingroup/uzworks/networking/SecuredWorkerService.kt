package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.response.WorkerResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SecuredWorkerService {

    @POST("api/Worker/Create")
    suspend fun createWorker(
        @Body workerCreateRequest: WorkerCreateRequest
    ): Response<WorkerResponse>

    @DELETE("api/Worker/Delete/{id}")
    suspend fun deleteWorker(
        @Path("id") workerId: String
    ): Response<Unit>

    @PUT("api/Worker/Edit")
    suspend fun editWorker(
        @Body workerEditRequest: WorkerEditRequest
    ): Response<WorkerResponse>
    
}