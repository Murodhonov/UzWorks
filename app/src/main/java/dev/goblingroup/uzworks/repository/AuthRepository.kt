package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.networking.AuthService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService
) {

    suspend fun login(loginRequest: LoginRequest) = authService.login(loginRequest)

    suspend fun signup(signUpRequest: SignUpRequest) = authService.signup(signUpRequest)
}