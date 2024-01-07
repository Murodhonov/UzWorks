package dev.goblingroup.uzworks.vm

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.utils.NetworkHelper

class LoginViewModelFactory(
    private val appDatabase: AppDatabase,
    private val authService: AuthService,
    private val networkHelper: NetworkHelper,
    var loginRequest: LoginRequest? = null,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            Log.d(
                "TAG",
                "create: updateCredentials loginRequest in ${this::class.java.simpleName} $loginRequest"
            )
            return LoginViewModel(
                appDatabase = appDatabase,
                authService = authService,
                networkHelper = networkHelper,
                loginRequest = loginRequest,
                context = context
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}