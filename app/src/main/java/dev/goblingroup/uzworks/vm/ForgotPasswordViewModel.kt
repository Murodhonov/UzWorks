package dev.goblingroup.uzworks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.ForgotPasswordRequest
import dev.goblingroup.uzworks.models.request.VerifyPhoneRequest
import dev.goblingroup.uzworks.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _forgotPasswordLiveData = MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())
    val forgotPasswordLiveData get() = _forgotPasswordLiveData

    private val _verifyPhoneLiveData = MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())
    val verifyPhoneLiveData get() = _verifyPhoneLiveData

    suspend fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest) {
        val response = authRepository.forgotPassword(forgotPasswordRequest)
        if (response.isSuccessful) {
            _forgotPasswordLiveData.postValue(ApiStatus.Success(response.body()))
        } else {
            _forgotPasswordLiveData.postValue(ApiStatus.Error(Throwable("Error")))
        }
    }

    suspend fun verifyPhone(verifyPhoneRequest: VerifyPhoneRequest) {
        val response = authRepository.verifyPhone(verifyPhoneRequest)
        if (response.isSuccessful) {
            _verifyPhoneLiveData.postValue(ApiStatus.Success(response.body()))
        } else {
            _verifyPhoneLiveData.postValue(ApiStatus.Error(Throwable("Error")))
        }
    }

}