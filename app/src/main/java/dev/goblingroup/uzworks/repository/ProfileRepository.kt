package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.request.UpdatePasswordRequest
import dev.goblingroup.uzworks.models.request.UserUpdateRequest
import dev.goblingroup.uzworks.networking.UserService
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