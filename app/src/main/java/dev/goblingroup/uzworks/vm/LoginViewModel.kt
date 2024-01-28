package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.database.entity.UserEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.repository.LoginRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val securityRepository: SecurityRepository,
    private val userDao: UserDao,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val loginStateFlow = MutableStateFlow<ApiStatus<LoginResponse>>(ApiStatus.Loading())

    fun login(loginRequest: LoginRequest? = null): StateFlow<ApiStatus<LoginResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Log.d(TAG, "login: loginRequest -> $loginRequest")
                loginRepository.login(getLoginRequest(loginRequest))
                    .catch {
                        loginStateFlow.emit(ApiStatus.Error(Throwable(it)))
                    }
                    .collect {
                        if (saveAuth(it)) {
                            if (userDao.getUser() == null)
                                loginRepository.addUser(it.mapToEntity(loginRequest!!))
                            loginStateFlow.emit(
                                ApiStatus.Success(
                                    response = it
                                )
                            )
                        }
                    }
            } else {
                loginStateFlow.emit(ApiStatus.Error(Throwable("No internet connection")))
            }
        }
        return loginStateFlow
    }

    private fun getLoginRequest(loginRequest: LoginRequest? = null): LoginRequest {
        var user: UserEntity? = null
        return try {
            user = loginRepository.getUser()
            if (user != null) {
                // room is not empty
                LoginRequest(user.username, user.password)
            } else {
                loginRequest ?: LoginRequest("", "")
            }
        } catch (e: Exception) {
            LoginRequest("", "")
        }

    }

    private fun saveAuth(loginResponse: LoginResponse): Boolean {
        val rolesSaved = securityRepository.setUserRoles(loginResponse.access)
        val tokenSaved = securityRepository.setToken(loginResponse.token)
        val userIdSaved = securityRepository.setUserId(loginResponse.userId)
        return tokenSaved && userIdSaved && rolesSaved
    }

    fun getUser() = loginRepository.getUser()

}