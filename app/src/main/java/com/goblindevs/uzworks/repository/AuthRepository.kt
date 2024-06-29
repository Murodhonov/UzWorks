package com.goblindevs.uzworks.repository

import com.goblindevs.uzworks.models.request.ForgotPasswordRequest
import com.goblindevs.uzworks.models.request.LoginRequest
import com.goblindevs.uzworks.models.request.ResetPasswordRequest
import com.goblindevs.uzworks.models.request.SignUpRequest
import com.goblindevs.uzworks.models.request.VerifyPhoneRequest
import com.goblindevs.uzworks.networking.AuthService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService
) {

    suspend fun login(loginRequest: LoginRequest) = authService.login(loginRequest)

    suspend fun signup(signUpRequest: SignUpRequest) = authService.signup(signUpRequest)

    suspend fun verifyPhone(verifyPhoneRequest: VerifyPhoneRequest) = authService.verifyPhone(verifyPhoneRequest)

    suspend fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest) = authService.forgotPassword(forgotPasswordRequest)

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest) = authService.resetPassword(resetPasswordRequest)
}