package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.models.request.ResetPasswordRequest
import dev.goblingroup.uzworks.models.request.UserUpdateRequest
import dev.goblingroup.uzworks.networking.SecuredUserService
import dev.goblingroup.uzworks.networking.UserService
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val userService: UserService,
    private val securedUserService: SecuredUserService,
    private val userDao: UserDao
) {

    suspend fun getUserById(userId: String) = securedUserService.getUserById(userId)

    suspend fun getRoles(userId: String) = userService.getRoles(userId)

    suspend fun updateUser(userUpdateRequest: UserUpdateRequest) = securedUserService.update(userUpdateRequest)

}