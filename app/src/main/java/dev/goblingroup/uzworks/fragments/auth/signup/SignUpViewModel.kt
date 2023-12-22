package dev.goblingroup.uzworks.fragments.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.repository.SignUpRepository
import dev.goblingroup.uzworks.resource.SignUpResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SignUpViewModel(
    authService: AuthService,
    private val networkHelper: NetworkHelper,
    private val signupRequest: SignUpRequest
) : ViewModel() {

    private val signupRepository = SignUpRepository(authService, signupRequest)
    private val signupStateFlow =
        MutableStateFlow<SignUpResource<Unit>>(SignUpResource.SignUpLoading())

    fun signup(): StateFlow<SignUpResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                signupRepository.signup()
                    .catch {
                        signupStateFlow.emit(SignUpResource.SignUpError(it))
                    }
                    .collect {
                        signupStateFlow.emit(SignUpResource.SignUpSuccess(it))
                    }
            } else {
                signupStateFlow.emit(SignUpResource.SignUpError(Throwable("No internet connection")))
            }
        }
        return signupStateFlow
    }

}