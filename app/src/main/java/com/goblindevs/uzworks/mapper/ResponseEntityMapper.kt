package com.goblindevs.uzworks.mapper

import com.goblindevs.uzworks.database.entity.AnnouncementEntity
import com.goblindevs.uzworks.database.entity.DistrictEntity
import com.goblindevs.uzworks.database.entity.JobCategoryEntity
import com.goblindevs.uzworks.database.entity.RegionEntity
import com.goblindevs.uzworks.models.response.DistrictResponse
import com.goblindevs.uzworks.models.response.JobCategoryResponse
import com.goblindevs.uzworks.models.response.JobResponse
import com.goblindevs.uzworks.models.response.RegionResponse
import com.goblindevs.uzworks.models.response.WorkerResponse
import com.goblindevs.uzworks.utils.AnnouncementEnum
import com.goblindevs.uzworks.utils.getImage

fun JobResponse.mapToEntity(position: Int): AnnouncementEntity {
    return AnnouncementEntity(
        id = id,
        categoryName = jobCategory.title,
        districtName = district.name,
        regionName = district.region.name,
        gender = gender,
        salary = salary,
        title = title,
        announcementType = AnnouncementEnum.JOB.announcementType,
        isTop = isTop,
        pictureResId = getImage(AnnouncementEnum.JOB.announcementType, gender, position),
    )
}

fun WorkerResponse.mapToEntity(): AnnouncementEntity {
    return AnnouncementEntity(
        id = id,
        categoryName = jobCategory.title,
        districtName = district.name,
        regionName = district.region.name,
        gender = gender,
        salary = salary,
        title = title,
        announcementType = AnnouncementEnum.WORKER.announcementType,
        isTop = isTop,
        pictureResId = getImage(AnnouncementEnum.WORKER.announcementType, gender, -1),
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