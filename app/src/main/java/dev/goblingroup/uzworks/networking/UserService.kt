package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.response.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {

    @GET("api/User/GetRoles/{id}")
    suspend fun getRoles(
        @Path("id") userId: String
    ): Response<List<String>>

}