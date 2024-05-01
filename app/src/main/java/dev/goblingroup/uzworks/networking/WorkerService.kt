package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.response.WorkerCreateResponse
import dev.goblingroup.uzworks.models.response.WorkerEditResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.ConstValues
import dev.goblingroup.uzworks.utils.ConstValues.AUTH
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkerService {

    @GET("api/Worker/GetById/{id}")
    suspend fun getById(
        @Path("id") workerId: String
    ): Response<WorkerResponse>

    @GET("api/Worker/GetAll")
    suspend fun getAll(
        @Query("pageNumber") pageNumber: Int? = null,
        @Query("pageSize") pageSize: Int = 10,
        @Query("jobCategoryId") jobCategoryId: String? = null,
        @Query("maxAge") maxAge: Int? = null,
        @Query("minAge") minAge: Int? = null,
        @Query("maxSalary") maxSalary: Int? = null,
        @Query("minSalary") minSalary: Int? = null,
        @Query("gender") gender: String? = null,
        @Query("regionId") regionId: String? = null,
        @Query("districtId") districtId: String? = null
    ): Response<List<WorkerResponse>>

    @GET("api/Worker/GetTopWorkers")
    suspend fun getTopWorkers(): Response<List<WorkerResponse>>

    @GET("api/Worker/GetCount/{status}")
    suspend fun countWorkers(
        @Path("status") status: Boolean = false
    ): Response<Int>

    @POST("api/Worker/Create")
    suspend fun createWorker(
        @Header(AUTH) token: String,
        @Body workerCreateRequest: WorkerCreateRequest
    ): Response<WorkerCreateResponse>

    @DELETE("api/Worker/Delete/{id}")
    suspend fun deleteWorker(
        @Header(AUTH) token: String,
        @Path("id") workerId: String
    ): Response<Unit>

    @PUT("api/Worker/Update")
    suspend fun editWorker(
        @Header(AUTH) token: String,
        @Body workerEditRequest: WorkerEditRequest
    ): Response<WorkerEditResponse>

    @GET("api/Worker/GetWorkersByUserId/{id}")
    suspend fun getWorkersByUserId(
        @Header(AUTH) token: String,
        @Path("id") userId: String
    ): Response<List<WorkerResponse>>

}