package dev.goblingroup.uzworks.mapper

import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.UserEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.LoginResponse

fun LoginResponse.mapToEntity(loginRequest: LoginRequest): UserEntity {
    return UserEntity(
        username = loginRequest.username,
        password = loginRequest.password,
        birthDate = this.birthDate,
        email = this.email,
        firstname = this.firstname,
        lastName = this.lastName,
        gender = this.gender,
        phoneNumber = this.phoneNumber
    )
}

fun JobResponse.mapToEntity(isSaved: Boolean): JobEntity {
    return JobEntity(
        this.id.toString(),
        this.benefit,
        this.categoryId,
        this.deadline,
        this.districtId,
        this.gender,
        this.instagramLink,
        this.latitude,
        this.longitude,
        this.maxAge,
        this.minAge,
        this.phoneNumber,
        this.requirement,
        this.salary,
        this.telegramLink,
        this.tgUserName,
        this.title,
        this.workingSchedule,
        this.workingTime,
        isSaved
    )
}