package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignupRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.networking.NetworkHelper
import dev.goblingroup.uzworks.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AuthViewModel(
    authService: AuthService,
    private val networkHelper: NetworkHelper,
    private val loginRequest: LoginRequest? = null,
    private val signupRequest: SignupRequest? = null
) : ViewModel() {

    private val authRepository = AuthRepository(authService, loginRequest, signupRequest)
    private val authStateFlow = MutableStateFlow<AuthResource<Unit>>(AuthResource.AuthLoading())

    fun login(): StateFlow<AuthResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                authRepository.login()
                    ?.catch {
                        authStateFlow.emit(AuthResource.AuthError(it))
                    }
                    ?.collect {
                        authStateFlow.emit(AuthResource.AuthSuccess(it))
                    }
            } else {
                authStateFlow.emit(AuthResource.AuthError(Throwable("No internet connection")))
            }
        }
        return authStateFlow
    }

    fun signup(): StateFlow<AuthResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                authRepository.signup()
                    ?.catch {
                        authStateFlow.emit(AuthResource.AuthError(it))
                    }
                    ?.collect {
                        authStateFlow.emit(AuthResource.AuthSuccess())
                    }
            } else {
                authStateFlow.emit(AuthResource.AuthError(Throwable("No internet connection")))
            }
        }
        return authStateFlow
    }

}