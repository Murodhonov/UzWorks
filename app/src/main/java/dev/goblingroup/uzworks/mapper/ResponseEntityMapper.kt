package dev.goblingroup.uzworks.mapper

import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.models.response.AnnouncementResponse
import dev.goblingroup.uzworks.models.response.DistrictResponse
import dev.goblingroup.uzworks.models.response.JobCategoryResponse
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.RegionResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.AnnouncementEnum
import dev.goblingroup.uzworks.utils.getImage

fun JobResponse.mapToEntity(): AnnouncementEntity {
    return AnnouncementEntity(
        id = id,
        categoryName = categoryName,
        districtName = districtName,
        gender = gender,
        salary = salary,
        title = title,
        announcementType = AnnouncementEnum.JOB.announcementType,
        isTop = isTop,
        pictureResId = getImage(AnnouncementEnum.JOB.announcementType, gender),
    )
}

fun WorkerResponse.mapToEntity(): AnnouncementEntity {
    return AnnouncementEntity(
        id = id,
        categoryName = categoryName,
        districtName = districtName,
        regionName = regionName,
        gender = gender,
        salary = salary,
        title = title,
        announcementType = AnnouncementEnum.WORKER.announcementType,
        isTop = isTop,
        pictureResId = getImage(AnnouncementEnum.WORKER.announcementType, gender),
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