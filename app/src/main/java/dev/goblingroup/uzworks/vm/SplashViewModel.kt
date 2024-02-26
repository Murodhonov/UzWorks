package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.repository.SplashRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashRepository: SplashRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SplashViewModel"

    private val _loginLiveData = MutableLiveData<ApiStatus<LoginResponse>>(ApiStatus.Loading())
    val loginLiveData get() = _loginLiveData

    init {
        login()
    }

    private fun login() {
        viewModelScope.launch {
            val user = splashRepository.getUser()
            if (user != null) {
                if (networkHelper.isNetworkConnected()) {
                    val response = splashRepository.login(
                        LoginRequest(
                            user.username,
                            user.password
                        )
                    )
                    if (response.isSuccessful) {
                        if (saveAuth(loginResponse = response.body()!!)) {
                            loginRepository.addUser(
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
                    _loginLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
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