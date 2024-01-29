package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.networking.AuthService
import javax.inject.Inject

class SplashRepository @Inject constructor(
    private val authService: AuthService,
    private val userDao: UserDao
) {

    suspend fun login(loginRequest: LoginRequest) = authService.login(loginRequest = loginRequest)

    suspend fun getUser() = userDao.getUser()

}