package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.utils.NetworkHelper

class SignUpViewModelFactory(
    private val authService: AuthService,
    private val networkHelper: NetworkHelper,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(
                authService = authService,
                networkHelper = networkHelper
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}