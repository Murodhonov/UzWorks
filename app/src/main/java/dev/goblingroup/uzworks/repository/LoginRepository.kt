package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.database.entity.UserEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.networking.AuthService
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val authService: AuthService,
    private val userDao: UserDao
) {

    suspend fun login(loginRequest: LoginRequest) = authService.login(loginRequest = loginRequest)

    fun addUser(userEntity: UserEntity) = userDao.addUser(userEntity)

    fun getUser() = userDao.getUser()

}