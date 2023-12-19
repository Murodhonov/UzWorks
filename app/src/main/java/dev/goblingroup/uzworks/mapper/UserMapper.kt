package dev.goblingroup.uzworks.mapper

import dev.goblingroup.uzworks.database.entity.UserEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse

fun LoginResponse.mapToEntity(loginRequest: LoginRequest): UserEntity {
    return UserEntity(
        username = loginRequest.username,
        password = loginRequest.password,
        roles = this.access,
        birthDate = this.birthDate,
        email = this.email,
        firstname = this.firstname,
        lastName = this.lastName,
        gender = this.gender,
        phoneNumber = this.phoneNumber
    )
}