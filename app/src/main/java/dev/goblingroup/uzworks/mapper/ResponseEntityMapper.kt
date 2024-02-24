package dev.goblingroup.uzworks.mapper

import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.JobCategoryResponse
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse

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