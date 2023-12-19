package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.database.entity.UserEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.networking.AuthService

class LoginRepository(
    private val authService: AuthService,
    private val loginRequest: LoginRequest,
    private val userDao: UserDao
) {

    fun login() = authService.login(loginRequest = loginRequest)

    fun getDatabaseUser() = userDao.getUser()

    fun addUser(userEntity: UserEntity) = userDao.addUser(userEntity)

}