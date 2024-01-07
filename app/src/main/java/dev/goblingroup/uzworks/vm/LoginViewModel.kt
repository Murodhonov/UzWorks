package dev.goblingroup.uzworks.vm

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.repository.LoginRepository
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.service.UserRoleImpl
import dev.goblingroup.uzworks.service.UserRoleService
import dev.goblingroup.uzworks.singleton.MySharedPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LoginViewModel(
    private val appDatabase: AppDatabase,
    authService: AuthService,
    private val networkHelper: NetworkHelper,
    private val loginRequest: LoginRequest? = null,
    private val context: Context
) : ViewModel() {

    private val TAG = "LoginViewModel"

    private var loginRepository =
        LoginRepository(
            authService = authService,
            loginRequest = getLoginRequest(),
            userDao = appDatabase.userDao()
        )
    private val loginStateFlow = MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private fun getLoginRequest(): LoginRequest {
        val user = appDatabase.userDao().getUser()
        return if (user != null) {
            // room is not empty
            LoginRequest(user.username, user.password)
        } else {
            loginRequest ?: LoginRequest("", "")
        }
    }

    fun login(): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Log.d(TAG, "login: loginRequest -> $loginRequest")
                loginRepository.login()
                    .catch {
                        loginStateFlow.emit(ApiStatus.Error(Throwable(it)))
                    }
                    .collect {
                        if (saveAuth(it)) {
                            if (appDatabase.userDao().getUser() == null)
                                loginRepository.addUser(it.mapToEntity(loginRequest!!))
                            loginStateFlow.emit(
                                ApiStatus.Success(
                                    response = it
                                )
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "couldn't save to shared preference",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                loginStateFlow.emit(ApiStatus.Error(Throwable("No internet connection")))
            }
        }
        return loginStateFlow
    }

    private fun saveAuth(loginResponse: LoginResponse): Boolean {
        val userRoleService: UserRoleService = UserRoleImpl()
        val rolesSaved = userRoleService.setUserRoles(loginResponse.access, context)
        val tokenSaved = MySharedPreference.getInstance(context).setToken(loginResponse.token)
        val userIdSaved = MySharedPreference.getInstance(context).setUserId(loginResponse.userId)
        return tokenSaved && userIdSaved && rolesSaved
    }

}