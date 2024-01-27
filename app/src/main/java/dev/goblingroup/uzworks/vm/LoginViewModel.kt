package dev.goblingroup.uzworks.vm

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.repository.LoginRepository
import dev.goblingroup.uzworks.service.UserRoleImpl
import dev.goblingroup.uzworks.service.UserRoleService
import dev.goblingroup.uzworks.singleton.MySharedPreference
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val userDao: UserDao,
    private val networkHelper: NetworkHelper,
    private val context: Context
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
        loginRepository
        val user = userDao.getUser()
        return if (user != null) {
            // room is not empty
            LoginRequest(user.username, user.password)
        } else {
            loginRequest ?: LoginRequest("", "")
        }
    }

    private fun saveAuth(loginResponse: LoginResponse): Boolean {
        val userRoleService: UserRoleService = UserRoleImpl()
        val rolesSaved = userRoleService.setUserRoles(loginResponse.access, context)
        val tokenSaved = MySharedPreference.getInstance(context).setToken(loginResponse.token)
        val userIdSaved = MySharedPreference.getInstance(context).setUserId(loginResponse.userId)
        return tokenSaved && userIdSaved && rolesSaved
    }

    fun getUser() = loginRepository.getUser()

}