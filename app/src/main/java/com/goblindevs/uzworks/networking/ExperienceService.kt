package com.goblindevs.uzworks.networking

import com.goblindevs.uzworks.models.request.ExperienceCreateRequest
import com.goblindevs.uzworks.models.request.ExperienceEditRequest
import com.goblindevs.uzworks.models.response.ExperienceCreateResponse
import com.goblindevs.uzworks.models.response.ExperienceEditResponse
import com.goblindevs.uzworks.models.response.ExperienceResponse
import com.goblindevs.uzworks.utils.ConstValues
import com.goblindevs.uzworks.utils.ConstValues.AUTH
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @DELETE("api/Experience/Delete/{id}")
    suspend fun deleteExperience(
        @Header(AUTH) token: String,
        @Path("id") experienceId: String
    ): Response<Unit>

}