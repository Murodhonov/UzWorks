package com.goblindevs.uzworks.repository

import com.goblindevs.uzworks.models.request.UpdatePasswordRequest
import com.goblindevs.uzworks.models.request.UserUpdateRequest
import com.goblindevs.uzworks.networking.UserService
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val userService: UserService,
    private val securityRepository: SecurityRepository
) {

    suspend fun getUserById(userId: String) = userService.getUserById(securityRepository.getToken(), userId)

    suspend fun updateUser(userUpdateRequest: UserUpdateRequest) = userService.update(securityRepository.getToken(), userUpdateRequest)

    suspend fun resetPassword(updatePasswordRequest: UpdatePasswordRequest) =
        userService.resetPassword(securityRepository.getToken(), updatePasswordRequest)
}