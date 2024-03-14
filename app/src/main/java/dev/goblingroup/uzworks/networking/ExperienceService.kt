package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.ExperienceCreateRequest
import dev.goblingroup.uzworks.models.request.ExperienceEditRequest
import dev.goblingroup.uzworks.models.response.ExperienceCreateResponse
import dev.goblingroup.uzworks.models.response.ExperienceEditResponse
import dev.goblingroup.uzworks.models.response.ExperienceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ExperienceService {

    @POST("api/Experience/CreateExperience")
    suspend fun createExperience(
        @Body experienceCreateRequest: ExperienceCreateRequest
    ): Response<ExperienceCreateResponse>

    @PUT("api/Experience/EditExperience")
    suspend fun editExperience(
        @Body experienceEditRequest: ExperienceEditRequest
    ): Response<ExperienceEditResponse>

    @GET("api/Experience/GetExperienceById/{id}")
    suspend fun getExperiencesByUserId(
        @Path("id") userId: String
    ): Response<List<ExperienceResponse>>

}