package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.repository.AuthRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.LanguageEnum
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val loginLiveData = MutableLiveData<ApiStatus<LoginResponse>>(ApiStatus.Loading())

    fun login(loginRequest: LoginRequest): LiveData<ApiStatus<LoginResponse>> {
        viewModelScope.launch(Dispatchers.IO) {
            if (networkHelper.isConnected()) {
                Log.d(TAG, "login: loginRequest -> $loginRequest")
                val response = authRepository.login(loginRequest)
                if (response.isSuccessful) {
                    if (saveAuth(response.body()!!)) {
                        authRepository.addUser(response.body()!!.mapToEntity(loginRequest))
                        loginLiveData.postValue(
                            ApiStatus.Success(
                                response = response.body()
                            )
                        )
                    }
                } else {
                    loginLiveData.postValue(ApiStatus.Error(Throwable("Some error in ${this@LoginViewModel::class.java.simpleName}")))
                    Log.e(TAG, "login: $response")
                    Log.e(TAG, "login: ${response.message()}")
                    Log.e(TAG, "login: ${response.code()}")
                    Log.e(TAG, "login: ${response.errorBody()}")
                }
            } else {
                loginLiveData.postValue(ApiStatus.Error(Throwable("No internet connection")))
            }
        }
        return loginLiveData
    }

    private fun saveAuth(loginResponse: LoginResponse): Boolean {
        val rolesSaved = securityRepository.setUserRoles(loginResponse.access)
        val tokenSaved = securityRepository.setToken(loginResponse.token)
        val userIdSaved = securityRepository.setUserId(loginResponse.userId)
        return tokenSaved && userIdSaved && rolesSaved
    }

    fun getLanguageCode() = securityRepository.getLanguageCode()

    fun setLanguageCode(languageCode: String?) {
        if (languageCode == null) securityRepository.setLanguageCode(LanguageEnum.KIRILL_UZB.code)
        else securityRepository.setLanguageCode(languageCode)
    }
}