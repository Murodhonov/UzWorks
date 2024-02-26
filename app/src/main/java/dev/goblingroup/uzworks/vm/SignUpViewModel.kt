package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.models.response.SignUpResponse
import dev.goblingroup.uzworks.repository.SignUpRepository
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val signUpLiveData =
        MutableLiveData<ApiStatus<SignUpResponse>>(ApiStatus.Loading())

    fun signup(signupRequest: SignUpRequest): LiveData<ApiStatus<SignUpResponse>> {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val response = signUpRepository.signup(signupRequest)
                if (response.isSuccessful) {
                    signUpLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    signUpLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                signUpLiveData.postValue(ApiStatus.Error(Throwable("No internet connection")))
            }
        }
        return signUpLiveData
    }

}