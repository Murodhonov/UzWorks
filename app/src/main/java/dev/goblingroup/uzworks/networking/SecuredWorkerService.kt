package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.request.WorkerRequest
import dev.goblingroup.uzworks.models.response.WorkerResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SecuredWorkerService {

    @POST("api/Worker/Create")
    fun createWorker(
        @Body workerRequest: WorkerRequest
    ): Flow<WorkerResponse>

    @DELETE("api/Worker/Delete/{id}")
    fun deleteWorker(
        @Path("id") workerId: String
    ): Flow<Response<Unit>>

    @PUT("api/Worker/Edit")
    fun editWorker(
        @Body workerEditRequest: WorkerEditRequest
    ): Flow<WorkerResponse>
    
}