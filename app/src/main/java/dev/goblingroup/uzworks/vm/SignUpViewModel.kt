package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.models.response.SignUpResponse
import dev.goblingroup.uzworks.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val signUpLiveData =
        MutableLiveData<ApiStatus<SignUpResponse>>(ApiStatus.Loading())

    fun signup(signupRequest: SignUpRequest): LiveData<ApiStatus<SignUpResponse>> {
        viewModelScope.launch {
            val response = authRepository.signup(signupRequest)
            if (response.isSuccessful) {
                signUpLiveData.postValue(ApiStatus.Success(response.body()))
            } else {
                signUpLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
            }
        }
        return signUpLiveData
    }

}