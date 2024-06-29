package com.goblindevs.uzworks.networking

import com.goblindevs.uzworks.models.request.UpdatePasswordRequest
import com.goblindevs.uzworks.models.request.UserUpdateRequest
import com.goblindevs.uzworks.models.response.UserResponse
import com.goblindevs.uzworks.models.response.UserUpdateResponse
import com.goblindevs.uzworks.utils.ConstValues.AUTH
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {

    @GET("api/User/GetById/{id}")
    suspend fun getUserById(
        @Header(AUTH) token: String,
        @Path("id") userId: String
    ): Response<UserResponse>

    @PUT("api/User/Update")
    suspend fun update(
        @Header(AUTH) token: String,
        @Body userUpdateRequest: UserUpdateRequest
    ): Response<UserUpdateResponse>

    @PUT("api/User/ResetPassword")
    suspend fun resetPassword(
        @Header(AUTH) token: String,
        @Body updatePasswordRequest: UpdatePasswordRequest
    ): Response<Unit>

    @GET("api/User/GetCount")
    suspend fun countUsers(
        @Header(AUTH) token: String
    ): Response<Int>

}