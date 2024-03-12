package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.repository.AuthRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val TAG = "SplashViewModel"

    private val _loginLiveData = MutableLiveData<ApiStatus<LoginResponse>>(ApiStatus.Loading())
    val loginLiveData get() = _loginLiveData

    init {
        login()
    }

    private fun login() {
        viewModelScope.launch {
            val user = securityRepository.getUser()
            if (user != null) {
                    val response = authRepository.login(LoginRequest(user.username, user.password))
                    if (response.isSuccessful) {
                        if (saveAuth(loginResponse = response.body()!!)) {
                            securityRepository.addUser(
                                response.body()!!
                                    .mapToEntity(LoginRequest(user.username, user.password))
                            )
                            _loginLiveData.postValue(ApiStatus.Success(response.body()))
                        }
                    } else {
                        _loginLiveData.postValue(ApiStatus.Error(Throwable("Some error on ${this@SplashViewModel::class.java.simpleName}")))
                        Log.e(TAG, "login: $response")
                        Log.e(TAG, "login: ${response.code()}")
                        Log.e(TAG, "login: ${response.message()}")
                        Log.e(TAG, "login: ${response.errorBody()}")
                    }
            } else {
                _loginLiveData.postValue(ApiStatus.Error(Throwable("User not found")))
            }
        }
    }

    private fun saveAuth(loginResponse: LoginResponse): Boolean {
        val rolesSaved = securityRepository.setUserRoles(loginResponse.access)
        val tokenSaved = securityRepository.setToken(loginResponse.token)
        val userIdSaved = securityRepository.setUserId(loginResponse.userId)
        return tokenSaved && userIdSaved && rolesSaved
    }

}