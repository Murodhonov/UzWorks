package dev.goblingroup.uzworks.fragments.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.models.request.SignupRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.networking.NetworkHelper
import dev.goblingroup.uzworks.repository.SignupRepository
import dev.goblingroup.uzworks.resource.SignupResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SignupViewModel(
    authService: AuthService,
    private val networkHelper: NetworkHelper,
    private val signupRequest: SignupRequest
) : ViewModel() {

    private val signupRepository = SignupRepository(authService, signupRequest)
    private val signupStateFlow =
        MutableStateFlow<SignupResource<Unit>>(SignupResource.SignupLoading())

    fun signup(): StateFlow<SignupResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                signupRepository.signup()
                    .catch {
                        signupStateFlow.emit(SignupResource.SignupError(it))
                    }
                    .collect {
                        signupStateFlow.emit(SignupResource.SignupSuccess(it))
                    }
            } else {
                signupStateFlow.emit(SignupResource.SignupError(Throwable("No internet connection")))
            }
        }
        return signupStateFlow
    }

}