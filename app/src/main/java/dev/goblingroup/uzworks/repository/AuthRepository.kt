package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.networking.AuthService

class AuthRepository(
    private val authService: AuthService,
    private val loginRequest: LoginRequest? = null,
    private val signupRequest: SignUpRequest? = null
) {

    fun login() = loginRequest?.let { authService.login(loginRequest = it) }

    fun signup() = signupRequest?.let { authService.signup(signupRequest = it) }

}