package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.models.response.SignUpResponse
import dev.goblingroup.uzworks.repository.SignUpRepository
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val signupStateFlow =
        MutableStateFlow<ApiStatus<SignUpResponse>>(ApiStatus.Loading())

    fun signup(signupRequest: SignUpRequest): StateFlow<ApiStatus<SignUpResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                signUpRepository.signup(signupRequest)
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