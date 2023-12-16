package dev.goblingroup.uzworks.fragments.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.networking.NetworkHelper
import dev.goblingroup.uzworks.repository.LoginRepository
import dev.goblingroup.uzworks.resource.LoginResource
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(
    authService: AuthService,
    private val networkHelper: NetworkHelper,
    private val loginRequest: LoginRequest
) : ViewModel() {

    private val loginRepository =
        LoginRepository(authService = authService, loginRequest = loginRequest)
    private val loginLiveData = MutableLiveData<LoginResource<Unit>>(LoginResource.LoginLoading())

    fun login(): LiveData<LoginResource<Unit>> {
        viewModelScope.launch {
            Log.d("TAG", "login: updateCredentials login request state $loginRequest")
            if (networkHelper.isNetworkConnected()) {
                loginRepository.login()
                    .catch {
                        val errorMessage =
                            if (it is IOException) "Network error" else "Error occurred"
                        loginLiveData.postValue(LoginResource.LoginError(Throwable(errorMessage)))
                    }
                    .collect {
                        loginLiveData.postValue(LoginResource.LoginSuccess(loginResponse = it))
                    }
            } else {
                loginLiveData.postValue(LoginResource.LoginError(Throwable("No internet connection")))
            }
        }
        return loginLiveData
    }

}