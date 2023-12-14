package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignupRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.networking.NetworkHelper

class AuthViewModelFactory(
    private val authService: AuthService,
    private val networkHelper: NetworkHelper,
    private val loginRequest: LoginRequest? = null,
    private val signupRequest: SignupRequest? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(
                authService = authService,
                networkHelper = networkHelper,
                loginRequest = loginRequest,
                signupRequest = signupRequest
            ) as T
        }
        return throw Exception("Some error in AuthViewModelFactory")
    }

}