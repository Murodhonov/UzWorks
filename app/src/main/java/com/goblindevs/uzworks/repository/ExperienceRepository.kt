package com.goblindevs.uzworks.repository

import com.goblindevs.uzworks.models.request.ExperienceCreateRequest
import com.goblindevs.uzworks.models.request.ExperienceEditRequest
import com.goblindevs.uzworks.networking.ExperienceService
import javax.inject.Inject

class ExperienceRepository @Inject constructor(
    private val experienceService: ExperienceService,
    private val securityRepository: SecurityRepository
) {

    suspend fun createExperience(experienceCreateRequest: ExperienceCreateRequest) =
        experienceService.createExperience(
            securityRepository.getToken(),
            experienceCreateRequest
        )

    suspend fun editExperience(experienceEditRequest: ExperienceEditRequest) =
        experienceService.updateExperience(
            securityRepository.getToken(),
            experienceEditRequest
        )

    suspend fun getExperiencesByUserId(userId: String) =
        experienceService.getExperiencesByUserId(
            securityRepository.getToken(),
            userId
        )

    suspend fun deleteExperience(experienceId: String) =
        experienceService.deleteExperience(securityRepository.getToken(), experienceId)
}