package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignupRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("Auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Flow<LoginResponse>

    @POST("Auth/signup")
    fun signup(
        @Body signupRequest: SignupRequest
    ): Flow<Response<Unit>>

}