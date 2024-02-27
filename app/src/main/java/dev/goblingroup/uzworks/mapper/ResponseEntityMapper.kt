package dev.goblingroup.uzworks.mapper

import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.database.entity.UserEntity
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.DistrictCreateResponse
import dev.goblingroup.uzworks.models.response.DistrictResponse
import dev.goblingroup.uzworks.models.response.JobCategoryCreateResponse
import dev.goblingroup.uzworks.models.response.JobCategoryResponse
import dev.goblingroup.uzworks.models.response.JobCreateResponse
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.models.response.RegionCreateResponse
import dev.goblingroup.uzworks.models.response.RegionResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse

fun LoginResponse.mapToEntity(loginRequest: LoginRequest): UserEntity {
    return UserEntity(
        username = loginRequest.username,
        password = loginRequest.password,
        birthDate = birthDate,
        email = email,
        firstname = firstname,
        lastName = lastName,
        gender = gender,
        phoneNumber = phoneNumber
    )
}

fun JobResponse.mapToEntity(isSaved: Boolean): JobEntity {
    return JobEntity(
        id,
        benefit,
        categoryId,
        deadline,
        districtId,
        gender,
        instagramLink,
        latitude,
        longitude,
        maxAge,
        minAge,
        phoneNumber,
        requirement,
        salary,
        telegramLink,
        tgUserName,
        title,
        workingSchedule,
        workingTime,
        isSaved
    )
}

fun JobCategoryResponse.mapToEntity(): JobCategoryEntity {
    return JobCategoryEntity(
        id = id,
        description = description,
        title = title
    )
}

fun RegionResponse.mapToEntity(): RegionEntity {
    return RegionEntity(
        id = id,
        name = name
    )
}

fun DistrictResponse.mapToEntity(regionId: String): DistrictEntity {
    return DistrictEntity(
        id = id,
        name = name,
        regionId = regionId
    )
}

fun WorkerResponse.mapToEntity(isSaved: Boolean): WorkerEntity {
    return WorkerEntity(
        id = id,
        birthDate = birthDate,
        categoryId = categoryId,
        createDate = createDate,
        createdBy = createdBy,
        deadline = deadline,
        districtId = districtId,
        fullName = fullName,
        gender = gender,
        instagramLink = instagramLink,
        location = location,
        phoneNumber = phoneNumber,
        salary = salary,
        telegramLink = telegramLink,
        tgUserName = tgUserName,
        title = title,
        userName = userName,
        workingSchedule = workingSchedule,
        workingTime = workingTime,
        isSaved = isSaved
    )
}