package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.request.ExperienceCreateRequest
import dev.goblingroup.uzworks.models.request.ExperienceEditRequest
import dev.goblingroup.uzworks.networking.ExperienceService
import javax.inject.Inject

class ExperienceRepository @Inject constructor(
    private val experienceService: ExperienceService
) {

    suspend fun createExperience(experienceCreateRequest: ExperienceCreateRequest) = experienceService.createExperience(experienceCreateRequest)

    suspend fun editExperience(experienceEditRequest: ExperienceEditRequest) = experienceService.updateExperience(experienceEditRequest)

    suspend fun getExperiencesByUserId(userId: String) = experienceService.getExperiencesByUserId(userId)

}