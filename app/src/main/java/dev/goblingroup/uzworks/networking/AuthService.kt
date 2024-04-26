package dev.goblingroup.uzworks.networking

import dev.goblingroup.uzworks.models.request.ForgotPasswordRequest
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.ResetPasswordRequest
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.models.request.UpdatePasswordRequest
import dev.goblingroup.uzworks.models.request.VerifyPhoneRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.models.response.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("Auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("Auth/signup")
    suspend fun signup(
        @Body signUpRequest: SignUpRequest
    ): Response<SignUpResponse>

    @POST("Auth/verify-phone")
    suspend fun verifyPhone(
        @Body verifyPhoneRequest: VerifyPhoneRequest
    ): Response<Unit>

    @POST("Auth/forget-password")
    suspend fun forgotPassword(
        @Body forgotPasswordRequest: ForgotPasswordRequest
    ): Response<Unit>

    @POST("Auth/reset-password")
    suspend fun resetPassword(
        @Body resetPasswordRequest: ResetPasswordRequest
    ): Response<Unit>

}