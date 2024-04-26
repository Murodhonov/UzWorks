package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.request.UpdatePasswordRequest
import dev.goblingroup.uzworks.models.request.UserUpdateRequest
import dev.goblingroup.uzworks.networking.SecuredUserService
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val securedUserService: SecuredUserService
) {

    suspend fun getUserById(userId: String) = securedUserService.getUserById(userId)

    suspend fun updateUser(userUpdateRequest: UserUpdateRequest) = securedUserService.update(userUpdateRequest)

    suspend fun resetPassword(updatePasswordRequest: UpdatePasswordRequest) =
        securedUserService.resetPassword(updatePasswordRequest)
}