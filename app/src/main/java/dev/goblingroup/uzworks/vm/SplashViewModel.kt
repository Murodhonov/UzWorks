package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.repository.AuthRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDao: UserDao,
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
            if (userDao.getUser() != null) {
                if (networkHelper.isConnected()) {
                    val response = authRepository.login(
                        LoginRequest(
                            userDao.getUser()?.username.toString(),
                            userDao.getUser()?.password.toString()
                        )
                    )
                    if (response.isSuccessful) {
                        _loginLiveData.postValue(ApiStatus.Success(response.body()))
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

}