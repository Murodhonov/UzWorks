package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.networking.AuthService

class LoginRepository(
    private val authService: AuthService,
    private val loginRequest: LoginRequest
) {

    fun login() = authService.login(loginRequest = loginRequest)

}