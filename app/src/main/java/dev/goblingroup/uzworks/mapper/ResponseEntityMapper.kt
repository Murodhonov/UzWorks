package dev.goblingroup.uzworks.mapper

import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.database.entity.UserEntity
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.DistrictResponse
import dev.goblingroup.uzworks.models.response.JobCategoryResponse
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.models.response.RegionResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.AnnouncementEnum

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

fun JobResponse.mapToEntity(isSaved: Boolean): AnnouncementEntity {
    return AnnouncementEntity(
        id = id,
        categoryId = categoryId,
        districtId = districtId,
        gender = gender,
        title = title,
        announcementType = AnnouncementEnum.JOB.announcementType,
        isSaved = isSaved,
        salary = salary
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

fun WorkerResponse.mapToEntity(isSaved: Boolean): AnnouncementEntity {
    return AnnouncementEntity(
        id = id,
        categoryId = categoryId,
        districtId = districtId,
        gender = gender,
        title = title,
        announcementType = AnnouncementEnum.WORKER.announcementType,
        isSaved = isSaved,
        salary = salary
    )
}