package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.database.entity.UserEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.networking.AuthService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val userDao: UserDao
) {

    suspend fun login(loginRequest: LoginRequest) = authService.login(loginRequest)

    suspend fun signup(signUpRequest: SignUpRequest) = authService.signup(signUpRequest)

    fun addUser(userEntity: UserEntity) {
        userDao.deleteUser()
        userDao.addUser(userEntity)
    }
}