package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.networking.AuthService

class SignUpRepository(
    private val authService: AuthService
) {

    fun signup(signUpRequest: SignUpRequest) = authService.signup(signUpRequest = signUpRequest)

}