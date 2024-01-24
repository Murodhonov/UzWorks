package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.repository.SignUpRepository
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SignUpViewModel(
    authService: AuthService,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val signupRepository = SignUpRepository(authService)
    private val signupStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    fun signup(signupRequest: SignUpRequest): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                signupRepository.signup(signupRequest)
                    .catch {
                        signupStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        signupStateFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                signupStateFlow.emit(ApiStatus.Error(Throwable("No internet connection")))
            }
        }
        return signupStateFlow
    }

}