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
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                appDatabase = appDatabase,
                authService = authService,
                networkHelper = networkHelper,
                context = context
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}