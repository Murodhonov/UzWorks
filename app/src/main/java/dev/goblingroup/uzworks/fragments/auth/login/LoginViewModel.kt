package dev.goblingroup.uzworks.fragments.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.networking.NetworkHelper
import dev.goblingroup.uzworks.repository.LoginRepository
import dev.goblingroup.uzworks.resource.LoginResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(
    authService: AuthService,
    private val networkHelper: NetworkHelper,
    loginRequest: LoginRequest
) : ViewModel() {

    private val loginRepository =
        LoginRepository(authService = authService, loginRequest = loginRequest)
    private val loginLiveData = MutableStateFlow<LoginResource<Unit>>(LoginResource.LoginLoading())

    fun login(): StateFlow<LoginResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                loginRepository.login()
                    .catch {
                        val errorMessage =
                            if (it is IOException) "Network error" else "Error occurred"
                        loginLiveData.emit(LoginResource.LoginError(Throwable(errorMessage)))
                    }
                    .collect {
                        loginLiveData.emit(LoginResource.LoginSuccess(loginResponse = it))
                    }
            } else {
                loginLiveData.emit(LoginResource.LoginError(Throwable("No internet connection")))
            }
        }
        return loginLiveData
    }

}