package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.request.SignupRequest
import dev.goblingroup.uzworks.networking.AuthService

class SignupRepository(
    private val authService: AuthService,
    private val signupRequest: SignupRequest
) {

    fun signup() = authService.signup(signupRequest = signupRequest)

}