package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.JobCreateRequest
import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.networking.SecuredJobService
import javax.inject.Inject

class SecuredJobRepository @Inject constructor(
    private val securedJobService: SecuredJobService
) {

    suspend fun createJob(jobCreateRequest: JobCreateRequest) =
        securedJobService.createJob(jobCreateRequest = jobCreateRequest)

    suspend fun deleteJob(jobId: String) = securedJobService.deleteJob(jobId = jobId)

    suspend fun editJob(jobEditRequest: JobEditRequest) =
        securedJobService.editJob(jobEditRequest = jobEditRequest)

}