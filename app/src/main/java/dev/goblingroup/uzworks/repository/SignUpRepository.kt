package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.networking.AuthService
import javax.inject.Inject

class SignUpRepository @Inject constructor(
    private val authService: AuthService
) {

    suspend fun signup(signUpRequest: SignUpRequest) = authService.signup(signUpRequest = signUpRequest)

}