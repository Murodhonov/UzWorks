package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.ResetPasswordRequest
import dev.goblingroup.uzworks.models.request.UserUpdateRequest
import dev.goblingroup.uzworks.models.response.ResetPasswordResponse
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.models.response.UserUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface SecuredUserService {

    @GET("api/User/GetUserById/{id}")
    suspend fun getUserById(
        @Path("id") userId: String
    ): Response<UserResponse>

    @PUT("api/User/Update")
    suspend fun update(
        @Body userUpdateRequest: UserUpdateRequest
    ): Response<UserUpdateResponse>

    @PUT("api/User/ResetPassword")
    suspend fun resetPassword(
        @Body resetPasswordRequest: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

}