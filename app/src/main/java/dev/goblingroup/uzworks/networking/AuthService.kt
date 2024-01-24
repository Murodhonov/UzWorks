package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.models.response.SignUpResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("Auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Flow<LoginResponse>

    @POST("Auth/signup")
    fun signup(
        @Body signUpRequest: SignUpRequest
    ): Flow<SignUpResponse>

}