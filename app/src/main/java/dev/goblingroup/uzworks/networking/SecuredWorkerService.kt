package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.ExperienceCreateRequest
import dev.goblingroup.uzworks.models.request.ExperienceEditRequest
import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.response.ExperienceCreateResponse
import dev.goblingroup.uzworks.models.response.ExperienceEditResponse
import dev.goblingroup.uzworks.models.response.ExperienceResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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

    @POST("api/Worker/CreateExperience")
    suspend fun createExperience(
        @Body experienceCreateRequest: ExperienceCreateRequest
    ): Response<ExperienceCreateResponse>

    @PUT("api/Worker/EditExperience")
    suspend fun editExperience(
        @Body experienceEditRequest: ExperienceEditRequest
    ): Response<ExperienceEditResponse>

    @GET("api/Worker/GetWorkersByUserId/{id}")
    suspend fun getWorkersByUserId(
        @Path("id") userId: String
    ): Response<List<WorkerResponse>>

    @GET("api/Worker/GetExperiencesByUserId/{id}")
    suspend fun getExperiencesByUserId(
        @Path("id") userId: String
    ): Response<List<ExperienceResponse>>

}