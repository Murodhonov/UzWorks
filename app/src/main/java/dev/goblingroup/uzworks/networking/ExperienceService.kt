package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.ExperienceCreateRequest
import dev.goblingroup.uzworks.models.request.ExperienceEditRequest
import dev.goblingroup.uzworks.models.response.ExperienceCreateResponse
import dev.goblingroup.uzworks.models.response.ExperienceEditResponse
import dev.goblingroup.uzworks.models.response.ExperienceResponse
import dev.goblingroup.uzworks.utils.ConstValues
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ExperienceService {

    @POST("api/Experience/Create")
    suspend fun createExperience(
        @Header(ConstValues.AUTH) token: String,
        @Body experienceCreateRequest: ExperienceCreateRequest
    ): Response<ExperienceCreateResponse>

    @PUT("api/Experience/Update")
    suspend fun updateExperience(
        @Header(ConstValues.AUTH) token: String,
        @Body experienceEditRequest: ExperienceEditRequest
    ): Response<ExperienceEditResponse>

    @GET("api/Experience/GetByUserId/{id}")
    suspend fun getExperiencesByUserId(
        @Header(ConstValues.AUTH) token: String,
        @Path("id") userId: String
    ): Response<List<ExperienceResponse>>

}