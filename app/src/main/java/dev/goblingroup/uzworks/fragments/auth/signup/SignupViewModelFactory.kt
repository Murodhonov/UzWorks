package dev.goblingroup.uzworks.fragments.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.models.request.SignupRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.networking.NetworkHelper

class SignupViewModelFactory(
    private val authService: AuthService,
    private val networkHelper: NetworkHelper,
    private val signupRequest: SignupRequest
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(
                authService = authService,
                networkHelper = networkHelper,
                signupRequest = signupRequest
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}