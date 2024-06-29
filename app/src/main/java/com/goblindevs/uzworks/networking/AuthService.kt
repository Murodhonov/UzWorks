package com.goblindevs.uzworks.networking

import com.goblindevs.uzworks.models.request.ForgotPasswordRequest
import com.goblindevs.uzworks.models.request.LoginRequest
import com.goblindevs.uzworks.models.request.ResetPasswordRequest
import com.goblindevs.uzworks.models.request.SignUpRequest
import com.goblindevs.uzworks.models.request.VerifyPhoneRequest
import com.goblindevs.uzworks.models.response.LoginResponse
import com.goblindevs.uzworks.models.response.SignUpResponse
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