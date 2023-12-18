package dev.goblingroup.uzworks.fragments.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.networking.NetworkHelper

class LoginViewModelFactory(
    private val authService: AuthService,
    private val networkHelper: NetworkHelper,
    var loginRequest: LoginRequest
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            Log.d(
                "TAG",
                "create: updateCredentials loginRequest in ${this::class.java.simpleName} $loginRequest"
            )
            return LoginViewModel(
                authService = authService,
                networkHelper = networkHelper,
                loginRequest = loginRequest
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}